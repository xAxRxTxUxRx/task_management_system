package com.artur.task_management_system.service.impl;

import com.artur.task_management_system.model.ConfirmationToken;
import com.artur.task_management_system.repository.ConfirmationTokenRepository;
import com.artur.task_management_system.service.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Сервис для доступа к базе данных ConfirmationToken.
 */
@Service
@AllArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;

    @Override
    public void saveConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationTokenRepository.save(confirmationToken);
    }

    @Override
    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    @Override
    public void deleteByUserId(Long userId){
        confirmationTokenRepository.deleteByUserId(userId);
    }
}
