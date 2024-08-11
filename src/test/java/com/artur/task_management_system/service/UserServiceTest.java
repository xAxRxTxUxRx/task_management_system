package com.artur.task_management_system.service;

import com.artur.task_management_system.exception.EmailTakenException;
import com.artur.task_management_system.exception.EntityNotFoundByIdException;
import com.artur.task_management_system.exception.UserNotFoundByEmailException;
import com.artur.task_management_system.model.User;
import com.artur.task_management_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ConfirmationTokenService confirmationTokenService;

    @Mock
    private UserConfirmationService userConfirmationService;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setName("Test User");
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        var userDetails = userService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
    }

    @Test
    void loadUserByUsername_UserDoesNotExist_ThrowsException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundByEmailException.class, () -> {
            userService.loadUserByUsername("test@example.com");
        });
    }

    @Test
    void getUserByEmail_UserExists_ReturnsUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        var result = userService.getUserByEmail("test@example.com");

        assertNotNull(result);
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void getUserByEmail_UserDoesNotExist_ThrowsException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundByEmailException.class, () -> {
            userService.getUserByEmail("test@example.com");
        });
    }

    @Test
    void getUserById_UserExists_ReturnsUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        var result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getUserById_UserDoesNotExist_ThrowsException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundByIdException.class, () -> {
            userService.getUserById(1L);
        });
    }

    @Test
    void addUser_EmailTaken_ThrowsException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(EmailTakenException.class, () -> {
            userService.addUser(user);
        });
    }

    @Test
    void addUser_Success_ReturnsConfirmationToken() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        var result = userService.addUser(user);

        assertNotNull(result);
        verify(userRepository).save(user);
    }

    @Test
    void updateUserById_UserExists_ReturnsOptionalWithToken() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        var result = userService.updateUserById(1L, user);

        assertTrue(result.isEmpty());
    }

    @Test
    void enableUser_UserExists_EnablesUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        userService.enableUser("test@example.com");

        assertTrue(user.isEnabled());
        verify(userRepository).save(user);
    }

    @Test
    void enableUser_UserDoesNotExist_ThrowsException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundByEmailException.class, () -> {
            userService.enableUser("test@example.com");
        });
    }
}
