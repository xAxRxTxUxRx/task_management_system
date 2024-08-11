package com.artur.task_management_system.service;

import com.artur.task_management_system.exception.*;
import com.artur.task_management_system.model.AuthenticationRequest;
import com.artur.task_management_system.model.AuthenticationResponse;
import com.artur.task_management_system.model.ConfirmationToken;
import com.artur.task_management_system.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserConfirmationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private ConfirmationTokenService confirmationTokenService;

    @Mock
    private EmailService emailService;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserConfirmationService userConfirmationService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    void testUpdateLoggedInUser() {
        User loggedInUser = new User();
        loggedInUser.setId(1L);
        loggedInUser.setEmail("user@example.com");

        User updatedUser = new User();
        updatedUser.setEmail("newemail@example.com");

        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setToken("token");

        when(securityContext.getAuthentication().getName()).thenReturn("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(loggedInUser);
        when(userService.updateUserById(1L, updatedUser)).thenReturn(Optional.of(confirmationToken));
        doNothing().when(confirmationTokenService).saveConfirmationToken(any(ConfirmationToken.class));

        userConfirmationService.updateLoggedInUser(updatedUser);

        verify(confirmationTokenService, times(1)).saveConfirmationToken(confirmationToken);
        verify(emailService, times(1)).sendConfirmationEmail(
                "newemail@example.com",
                updatedUser.getName(),
                "http://localhost:8080/api/auth/confirm?token=token"
        );
    }

    @Test
    void testRegisterUser() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setName("User");

        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setToken("token");

        when(userService.addUser(user)).thenReturn(confirmationToken);
        doNothing().when(confirmationTokenService).saveConfirmationToken(any(ConfirmationToken.class));
        when(jwtService.generateToken(user)).thenReturn("jwt");

        AuthenticationResponse response = userConfirmationService.registerUser(user);

        assertEquals("jwt", response.getJwt());
        assertEquals("token", response.getEmailToken());
        verify(confirmationTokenService, times(1)).saveConfirmationToken(confirmationToken);
        verify(emailService, times(1)).sendConfirmationEmail(
                "user@example.com",
                "User",
                "http://localhost:8080/api/auth/confirm?token=token"
        );
    }

    @Test
    void testAuthenticate() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("user@example.com");
        request.setPassword("password");

        User user = new User();
        user.setEmail("user@example.com");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userService.getUserByEmail("user@example.com")).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt");

        AuthenticationResponse response = userConfirmationService.authenticate(request);

        assertEquals("jwt", response.getJwt());
    }

    @Test
    void testAuthenticate_ThrowsBadCredentialsException() {
        AuthenticationRequest request = new AuthenticationRequest();
        request.setEmail("user@example.com");
        request.setPassword("wrongpassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> userConfirmationService.authenticate(request));
    }

    @Test
    void testConfirmToken() {
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setToken("token");
        confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        confirmationToken.setUser(User.builder().email("user@example.com").build());
        confirmationToken.setConfirmedAt(null);

        when(confirmationTokenService.getToken("token")).thenReturn(Optional.of(confirmationToken));
        doNothing().when(userService).enableUser("user@example.com");

        userConfirmationService.confirmToken("token");

        assertNotNull(confirmationToken.getConfirmedAt());
        verify(userService, times(1)).enableUser("user@example.com");
    }

    @Test
    void testConfirmToken_ThrowsConfirmationTokenNotFoundException() {
        when(confirmationTokenService.getToken("token")).thenReturn(Optional.empty());

        assertThrows(ConfirmationTokenNotFoundException.class, () -> userConfirmationService.confirmToken("token"));
    }

    @Test
    void testConfirmToken_ThrowsConfirmationTokenConfirmedException() {
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setToken("token");
        confirmationToken.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        confirmationToken.setConfirmedAt(LocalDateTime.now());

        when(confirmationTokenService.getToken("token")).thenReturn(Optional.of(confirmationToken));

        assertThrows(ConfirmationTokenConfirmedException.class, () -> userConfirmationService.confirmToken("token"));
    }

    @Test
    void testConfirmToken_ThrowsConfirmationTokenExpiredException() {
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setToken("token");
        confirmationToken.setExpiresAt(LocalDateTime.now().minusMinutes(10));
        confirmationToken.setConfirmedAt(null);

        when(confirmationTokenService.getToken("token")).thenReturn(Optional.of(confirmationToken));

        assertThrows(ConfirmationTokenExpiredException.class, () -> userConfirmationService.confirmToken("token"));
    }
}
