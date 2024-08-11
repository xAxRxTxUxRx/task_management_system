package com.artur.task_management_system.dto;


import com.artur.task_management_system.model.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserViewDTO {
    private String email;
    private String name;
    private Set<Task> createdTasks;
    private Set<Task> assignedTasks;
}
