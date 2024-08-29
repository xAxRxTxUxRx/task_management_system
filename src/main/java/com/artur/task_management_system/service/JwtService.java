package com.artur.task_management_system.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

public interface JwtService {
    /**
     * Извлекает имя пользователя из JWT токена.
     *
     * @param jwt JWT токен
     * @return имя пользователя, указанное в токене
     */
    String extractUsername(String jwt);

    /**
     * Извлекает конкретный claim из JWT токена.
     *
     * @param jwt JWT токен
     * @param claimsResolver функция, которая преобразует claim в нужный тип
     * @param <T> тип результата
     * @return значение claim
     */
    <T> T extractClaim(String jwt,
                       Function<Claims, T> claimsResolver);

    /**
     * Генерирует JWT токен для пользователя.
     *
     * @param userDetails детали пользователя
     * @return JWT токен
     */
    String generateToken(UserDetails userDetails);

    /**
     * Генерирует JWT токен для пользователя с дополнительными claims.
     *
     * @param extraClaims дополнительные claims для токена
     * @param userDetails детали пользователя
     * @return JWT токен
     */
    String generateToken(Map<String, Object> extraClaims,
                         UserDetails userDetails);

    /**
     * Проверяет, действителен ли JWT токен для данного пользователя.
     *
     * @param jwt JWT токен
     * @param userDetails детали пользователя
     * @return true, если токен действителен, иначе false
     */
    Boolean isTokenValid(String jwt, UserDetails userDetails);
}
