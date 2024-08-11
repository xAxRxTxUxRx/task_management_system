package com.artur.task_management_system.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Сервис для работы с JWT.
 */
@Service
public class JwtService {
    private final long EXPIRATION_TIME = 1000 * 24 * 60;

    @Value("${artur.secret-key}")
    private String SECRET_KEY;

    /**
     * Извлекает имя пользователя из JWT токена.
     *
     * @param jwt JWT токен
     * @return имя пользователя, указанное в токене
     */
    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    /**
     * Извлекает конкретный claim из JWT токена.
     *
     * @param jwt JWT токен
     * @param claimsResolver функция, которая преобразует claim в нужный тип
     * @param <T> тип результата
     * @return значение claim
     */
    public <T> T extractClaim(String jwt,
                              Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }

    /**
     * Генерирует JWT токен для пользователя.
     *
     * @param userDetails детали пользователя
     * @return JWT токен
     */
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Генерирует JWT токен для пользователя с дополнительными claims.
     *
     * @param extraClaims дополнительные claims для токена
     * @param userDetails детали пользователя
     * @return JWT токен
     */
    public String generateToken(Map<String, Object> extraClaims,
                                UserDetails userDetails){


        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Проверяет, действителен ли JWT токен для данного пользователя.
     *
     * @param jwt JWT токен
     * @param userDetails детали пользователя
     * @return true, если токен действителен, иначе false
     */
    public Boolean isTokenValid(String jwt, UserDetails userDetails){
        final String username = extractUsername(jwt);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(jwt));
    }

    /**
     * Проверяет, истек ли срок действия JWT токена.
     *
     * @param jwt JWT токен
     * @return true, если токен истек, иначе false
     */
    private boolean isTokenExpired(String jwt) {
        return extractClaim(jwt, Claims::getExpiration).before(new Date());
    }

    /**
     * Извлекает все claims из JWT токена.
     *
     * @param jwt JWT токен
     * @return все claims из токена
     */
    private Claims extractAllClaims(String jwt){
        return Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    /**
     * Получает секретный ключ для подписи JWT токена.
     *
     * @return секретный ключ
     */
    private SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
