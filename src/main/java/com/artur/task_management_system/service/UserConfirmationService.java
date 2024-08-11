package com.artur.task_management_system.service;

import com.artur.task_management_system.exception.*;
import com.artur.task_management_system.model.AuthenticationRequest;
import com.artur.task_management_system.model.AuthenticationResponse;
import com.artur.task_management_system.model.ConfirmationToken;
import com.artur.task_management_system.model.User;
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
public class UserConfirmationService {
    private final UserService userService;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Обновляет информацию о текущем аутентифицированном пользователе.
     *
     * Метод получает текущего аутентифицированного пользователя, обновляет его информацию с помощью предоставленных данных,
     * сохраняет новые данные пользователя в базе данных и, если был изменен email, генерирует новый токен подтверждения
     * и отправляет письмо с ссылкой для подтверждения нового адреса электронной почты.
     *
     * @param user объект User, содержащий обновленные данные текущего аутентифицированного пользователя
     * @throws UserNotFoundByEmailException если текущий аутентифицированный пользователь не найден
     * @throws EmailTakenException если новый email уже используется другим пользователем
     * @throws ConfirmationTokenNotFoundException если токен подтверждения не найден
     */
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

    /**
     * Регистрирует нового пользователя в системе.
     *
     * Метод добавляет нового пользователя через сервис пользователя, сохраняет токен подтверждения,
     * генерирует JWT токен для аутентификации пользователя и отправляет письмо с ссылкой для подтверждения
     * регистрации на электронную почту пользователя.
     *
     * @param user объект User, содержащий данные нового пользователя
     * @return объект AuthenticationResponse, содержащий сгенерированный JWT токен и токен подтверждения
     * @throws EmailTakenException если электронная почта уже используется другим пользователем
     */
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

    /**
     * Аутентифицирует пользователя на основе предоставленных учетных данных.
     *
     * Метод выполняет аутентификацию пользователя с использованием менеджера аутентификации,
     * проверяя имя пользователя и пароль. После успешной аутентификации генерируется JWT токен
     * для пользователя и возвращается ответ аутентификации, содержащий этот токен.
     *
     * @param request объект AuthenticationRequest, содержащий имя пользователя (электронную почту) и пароль
     * @return объект AuthenticationResponse, содержащий сгенерированный JWT токен
     * @throws BadCredentialsException если имя пользователя или пароль неверны
     * @throws DisabledException если учетная запись пользователя отключена
     * @throws LockedException если учетная запись пользователя заблокирована
     * @throws AccountExpiredException если срок действия учетной записи пользователя истек
     * @throws CredentialsExpiredException если срок действия учетных данных пользователя истек
     */
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

    /**
     * Подтверждает токен пользователя, активируя его аккаунт.
     *
     * Этот метод проверяет наличие токена, его срок действия и то, был ли он уже подтвержден.
     * Если все условия удовлетворены, пользователь, ассоциированный с токеном, активируется.
     *
     * @param token строка токена для подтверждения
     * @throws ConfirmationTokenNotFoundException если токен не найден
     * @throws ConfirmationTokenConfirmedException если токен уже был подтвержден
     * @throws ConfirmationTokenExpiredException если срок действия токена истек
     */
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
