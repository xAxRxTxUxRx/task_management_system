package com.artur.task_management_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskCommentCreationDTO {
    @NotBlank(message = "Text is mandatory")
    private String text;
}
