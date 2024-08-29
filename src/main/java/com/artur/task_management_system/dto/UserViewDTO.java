package com.artur.task_management_system.dto;


import com.artur.task_management_system.model.Task;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@OpenAPIDefinition(
        info = @Info(
                title = "User View DTO",
                description = "Data Transfer Object for User view"
        )
)
public class UserViewDTO {
    @Schema(description = "User email address")
    private String email;

    @Schema(description = "User name")
    private String name;

    @Schema(description = "Tasks created by the user", type = "array")
    private Set<Task> createdTasks;

    @Schema(description = "Tasks assigned to the user", type = "array")
    private Set<Task> assignedTasks;
}
