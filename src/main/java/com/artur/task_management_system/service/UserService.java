package com.artur.task_management_system.service;

import com.artur.task_management_system.exception.EmailTakenException;
import com.artur.task_management_system.exception.EntityNotFoundByIdException;
import com.artur.task_management_system.exception.UserNotFoundByEmailException;
import com.artur.task_management_system.model.ConfirmationToken;
import com.artur.task_management_system.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

/**
 * Сервис для работы с User.
 * Реализует UserDetailsService.
 */
public interface UserService extends UserDetailsService {

    /**
     * Возвращает пользователя по его электронной почте.
     *
     * @param email электронная почта пользователя
     * @return объект User, представляющий пользователя
     * @throws UserNotFoundByEmailException если пользователь не найден
     */
    User getUserByEmail(String email);

    /**
     * Возвращает страницу пользователей с учетом пагинации и сортировки.
     *
     * @param pageNumber номер страницы для пагинации
     * @param pageSize количество элементов на странице
     * @param field поле для сортировки
     * @param directionStr направление сортировки ("Asc" или "Desc")
     * @return объект Page, содержащий пользователей
     */
    Page<User> getAllUsers(int pageNumber, int pageSize,
                           String field, String directionStr);

    /**
     * Возвращает пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return объект User, представляющий пользователя
     * @throws EntityNotFoundByIdException если пользователь не найден
     */
    User getUserById(Long userId);

    /**
     * Удаляет текущего аутентифицированного пользователя из системы.
     */
    @Transactional
    void deleteLoggedInUser();

    /**
     * Добавляет нового пользователя в систему.
     *
     * @param user объект User, содержащий данные нового пользователя
     * @return объект ConfirmationToken, связанный с новым пользователем
     * @throws EmailTakenException если электронная почта уже используется другим пользователем
     */
    ConfirmationToken addUser(User user);

    /**
     * Обновляет информацию о пользователе по его идентификатору.
     *
     * @param userId идентификатор пользователя для обновления
     * @param user объект User с новыми данными пользователя
     * @return объект Optional, содержащий ConfirmationToken, если был изменен email, иначе пустой
     * @throws EmailTakenException если новый email уже используется другим пользователем
     */
    Optional<ConfirmationToken> updateUserById(Long userId, User user);

    /**
     * Активирует пользователя по его электронной почте.
     *
     * @param email электронная почта пользователя для активации
     * @throws UserNotFoundByEmailException если пользователь не найден
     */
    void enableUser(String email);
}
