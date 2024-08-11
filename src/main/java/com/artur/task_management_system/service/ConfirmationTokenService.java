package com.artur.task_management_system.service;

import com.artur.task_management_system.model.ConfirmationToken;
import com.artur.task_management_system.repository.ConfirmationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис для доступа к базе данных ConfirmationToken.
 */
@Service
@AllArgsConstructor
public class ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    /**
     * Сохраняет токен подтверждения в репозитории.
     *
     * @param confirmationToken объект ConfirmationToken, который нужно сохранить
     */
    public void saveConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationTokenRepository.save(confirmationToken);
    }

    /**
     * Ищет токен подтверждения по его значению.
     *
     * @param token значение токена для поиска
     * @return Optional объекта ConfirmationToken, если найден
     */
    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    /**
     * Удаляет все токены подтверждения, связанные с определенным пользователем.
     *
     * @param userId идентификатор пользователя, для которого нужно удалить токены
     */
    public void deleteByUserId(Long userId){
        confirmationTokenRepository.deleteByUserId(userId);
    }
}
