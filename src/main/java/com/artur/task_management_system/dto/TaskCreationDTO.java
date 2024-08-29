package com.artur.task_management_system.dto;

import com.artur.task_management_system.model.attributes.TaskPriority;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@OpenAPIDefinition(
        info = @Info(
                title = "Task Creation DTO",
                description = "Data Transfer Object for Task creation"
        )
)
public class TaskCreationDTO {
    @Schema(description = "The title of the task")
    @NotBlank(message = "Title is mandatory")
    private String title;

    @Schema(description = "A brief description of the task")
    @NotBlank(message = "Description is mandatory")
    private String description;

    @Schema(description = "The priority level of the task",
            allowableValues = {"LOW", "MEDIUM", "HIGH"})
    @NotNull(message = "Task priority is mandatory")
    private TaskPriority priority;

    @Schema(description = "The deadline for completing the task",
            format = "date-time")
    @Future(message = "Deadline has to be in future")
    private LocalDateTime deadLineDate;

    @Schema(description = "Set of user IDs assigned to perform this task")
    private Set<Long> performersIds = new HashSet<>();
}