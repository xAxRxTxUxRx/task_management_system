package com.artur.task_management_system.dto.mappers;

import com.artur.task_management_system.dto.TaskViewDTO;
import com.artur.task_management_system.model.Task;
import org.mapstruct.Mapper;

@Mapper
public interface TaskMapper {
    TaskViewDTO taskToTaskViewDTO(Task task);
}
