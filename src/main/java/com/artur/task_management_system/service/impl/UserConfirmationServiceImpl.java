package com.artur.task_management_system.service.impl;

import com.artur.task_management_system.exception.*;
import com.artur.task_management_system.model.AuthenticationRequest;
import com.artur.task_management_system.model.AuthenticationResponse;
import com.artur.task_management_system.model.ConfirmationToken;
import com.artur.task_management_system.model.User;
import com.artur.task_management_system.service.*;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Сервис для работы с регистрацией, активацией и авторизацией пользователя.
 */
@Service
@AllArgsConstructor
public class UserConfirmationServiceImpl implements UserConfirmationService {
    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public void updateLoggedInUser(User user) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User logedInUser = userService.getUserByEmail(username);
        Optional<ConfirmationToken> confirmationTokenOptional = userService.updateUserById(logedInUser.getId(), user);
        if (confirmationTokenOptional.isPresent()){
            ConfirmationToken confirmationToken = confirmationTokenOptional.get();
            confirmationTokenService.saveConfirmationToken(confirmationToken);

            String token = confirmationToken.getToken();
            String link = "http://localhost:8080/api/auth/confirm?token=" + token;
            emailService.sendConfirmationEmail(
                    user.getEmail(),
                    user.getName(),
                    link
            );
        }
    }

    @Override
    public AuthenticationResponse registerUser(User user) {
        ConfirmationToken confirmationToken = userService.addUser(user);
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        String jwt = jwtService.generateToken(user);

        String emailToken = confirmationToken.getToken();
        String link = "http://localhost:8080/api/auth/confirm?token=" + emailToken;
        emailService.sendConfirmationEmail(
                user.getEmail(),
                user.getName(),
                link
        );
        return new AuthenticationResponse(jwt, emailToken);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userService.getUserByEmail(request.getEmail());
        String jwt = jwtService.generateToken(user);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setJwt(jwt);
        return authenticationResponse;
    }

    @Override
    public void confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token)
                .orElseThrow(() -> new ConfirmationTokenNotFoundException(token));

        if (confirmationToken.getConfirmedAt() != null){
            throw new ConfirmationTokenConfirmedException(token);
        }

        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new ConfirmationTokenExpiredException(token);
        }

        confirmationToken.setConfirmedAt(LocalDateTime.now());
        userService.enableUser(confirmationToken.getUser().getEmail());
    }
}
