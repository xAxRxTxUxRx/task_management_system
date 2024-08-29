package com.artur.task_management_system.service;

import com.artur.task_management_system.exception.*;
import com.artur.task_management_system.model.AuthenticationRequest;
import com.artur.task_management_system.model.AuthenticationResponse;
import com.artur.task_management_system.model.User;
import org.springframework.security.authentication.*;

public interface UserConfirmationService {
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
    void updateLoggedInUser(User user);

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
    AuthenticationResponse registerUser(User user);

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
    AuthenticationResponse authenticate(AuthenticationRequest request);

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
    void confirmToken(String token);
}
