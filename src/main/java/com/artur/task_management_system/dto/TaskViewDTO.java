package com.artur.task_management_system.dto;

import com.artur.task_management_system.model.TaskComment;
import com.artur.task_management_system.model.User;
import com.artur.task_management_system.model.attributes.TaskPriority;
import com.artur.task_management_system.model.attributes.TaskStatus;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@OpenAPIDefinition(
        info = @Info(
                title = "Task View DTO",
                description = "Data Transfer Object for Task view"
        )
)
public class TaskViewDTO {
    @Schema(description = "The title of the task")
    private String title;

    @Schema(description = "A brief description of the task")
    private String description;

    @Schema(description = "The current status of the task",
            allowableValues = {"IN_PROGRESS", "NEW", "COMPLETED"})
    private TaskStatus status;

    @Schema(description = "The priority level of the task",
            allowableValues = {"LOW", "MEDIUM", "HIGH"})
    private TaskPriority priority;

    @Schema(description = "The date when the task was created")
    private LocalDateTime creationDate;

    @Schema(description = "The deadline for completing the task")
    private LocalDateTime deadLineDate;

    @Schema(description = "The user who created this task")
    private User author;

    @Schema(description = "Set of users assigned to perform this task")
    private Set<User> performers;

    @Schema(description = "Set of comments related to this task")
    private Set<TaskComment> comments;
}