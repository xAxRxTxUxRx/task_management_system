package com.artur.task_management_system.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ответ аутентифицаии.
 * Создается при регистрации и авторизации.
 * При авторизации emailToken = null.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String jwt;
    private String emailToken;
}
