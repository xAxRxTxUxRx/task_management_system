package com.artur.task_management_system.dto;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@OpenAPIDefinition(
        info = @Info(
                title = "User Creation DTO",
                description = "Data Transfer Object for User creation"
        )
)
public class UserCreationDTO {
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email field must match email pattern")
    @Schema(
            description = "The email address of the user.",
            pattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z]{2,})$"
    )
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Pattern(regexp = "^(?=.*\\d)[A-Za-z\\d]{7,}$", message = "Password must match pattern")
    @Schema(
            description = "The password for the user account.",
            pattern = "^(?=.*\\d)[A-Za-z\\d]{7,}$"
    )
    private String password;

    @NotBlank(message = "Matching password is mandatory")
    @Pattern(regexp = "^(?=.*\\d)[A-Za-z\\d]{7,}$", message = "Password must match pattern")
    @Schema(
            description = "Confirmation of the password.",
            pattern = "^(?=.*\\d)[A-Za-z\\d]{7,}$"
    )
    private String matchingPassword;

    @NotBlank(message = "Name is mandatory")
    @Schema(
            description = "The name of the user."
    )
    private String name;
}
