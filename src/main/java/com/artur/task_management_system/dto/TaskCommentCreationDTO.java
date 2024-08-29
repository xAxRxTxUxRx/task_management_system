package com.artur.task_management_system.dto;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@OpenAPIDefinition(
        info = @Info(
                title = "Task Comment Creation DTO",
                description = "Data Transfer Object for Task Comment creation"
        )
)
public class TaskCommentCreationDTO {
    @Schema(description = "A text value for comment creation")
    @NotBlank(message = "Text is mandatory")
    private String text;
}
