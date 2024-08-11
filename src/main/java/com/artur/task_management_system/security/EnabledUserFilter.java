package com.artur.task_management_system.security;

import com.artur.task_management_system.exception.UserNotEnabledException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фильтер для SecurityFilterChain.
 * Проверяет активирован ли аккаунт пользователя.
 */
@Component
@AllArgsConstructor
public class EnabledUserFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());
            if (!userDetails.isEnabled()){
                throw new UserNotEnabledException();
            }
        }
        filterChain.doFilter(request, response);
    }
}
