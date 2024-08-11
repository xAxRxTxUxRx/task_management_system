package com.artur.task_management_system.openapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Класс конфигурации OpenAPI.
 */
@Configuration
public class OpenAPIConfig {
    @Value("${artur.openapi.dev-url}")
    private String devUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("Server URL in Development environment");

        Contact contact = new Contact();
        contact.setEmail("khramchenkov.ciber@gmail.com");
        contact.setName("Artur");

        License mitLicense = new License().name("MIT License").url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Task Management System")
                .version("1.0")
                .contact(contact)
                .description("This API exposes endpoints to manage task management system. Test task for Effectiv Mobile")
                .license(mitLicense);

        return new OpenAPI().info(info).servers(List.of(devServer));
    }
}
