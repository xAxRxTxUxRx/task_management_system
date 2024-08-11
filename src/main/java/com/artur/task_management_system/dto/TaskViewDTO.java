package com.artur.task_management_system.dto;

import com.artur.task_management_system.model.TaskComment;
import com.artur.task_management_system.model.User;
import com.artur.task_management_system.model.attributes.TaskPriority;
import com.artur.task_management_system.model.attributes.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskViewDTO {
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDateTime creationDate;
    private LocalDateTime deadLineDate;
    private User author;
    private Set<User> performers;
    private Set<TaskComment> comments;
}
