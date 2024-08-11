package com.artur.task_management_system.dto;

import com.artur.task_management_system.model.attributes.TaskPriority;
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
public class TaskCreationDTO {
    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotNull(message = "Task priority is mandatory")
    private TaskPriority priority;

    @Future(message = "Deadline has to be in future")
    private LocalDateTime deadLineDate;

    private Set<Long> performersIds = new HashSet<>();
}
