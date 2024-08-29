package com.artur.task_management_system.service;

import com.artur.task_management_system.model.ConfirmationToken;

import java.util.Optional;

public interface ConfirmationTokenService {
    /**
     * Сохраняет токен подтверждения в репозитории.
     *
     * @param confirmationToken объект ConfirmationToken, который нужно сохранить
     */
    void saveConfirmationToken(ConfirmationToken confirmationToken);

    /**
     * Ищет токен подтверждения по его значению.
     *
     * @param token значение токена для поиска
     * @return Optional объекта ConfirmationToken, если найден
     */
    Optional<ConfirmationToken> getToken(String token);

    /**
     * Удаляет все токены подтверждения, связанные с определенным пользователем.
     *
     * @param userId идентификатор пользователя, для которого нужно удалить токены
     */
    void deleteByUserId(Long userId);
}
