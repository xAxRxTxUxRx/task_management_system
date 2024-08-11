package com.artur.task_management_system.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

/**
 * Bean DelegatingPasswordEncoder реализущего интерфейс PasswordEncoder.
 */
@Configuration
public class PasswordEncoder {

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder delegatingPasswordEncoder (){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
