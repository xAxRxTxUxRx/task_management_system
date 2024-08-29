package com.artur.task_management_system.service.impl;

import com.artur.task_management_system.dto.TaskCommentCreationDTO;
import com.artur.task_management_system.exception.UnauthenticatedException;
import com.artur.task_management_system.model.TaskComment;
import com.artur.task_management_system.model.User;
import com.artur.task_management_system.repository.TaskCommentRepository;
import com.artur.task_management_system.service.TaskCommentService;
import com.artur.task_management_system.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Сервис для работы с комментариями к задачам.
 */
@Service
@AllArgsConstructor
public class TaskCommentServiceImpl implements TaskCommentService {
    private final TaskCommentRepository taskCommentRepository;
    private final UserService userService;

    @Override
    public TaskComment addTaskComment(TaskCommentCreationDTO taskCommentDTO) {
        TaskComment comment = new TaskComment();

        if (SecurityContextHolder.getContext().getAuthentication() == null){
            throw new UnauthenticatedException();
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User fromUser = userService.getUserByEmail(username);

        comment.setText(taskCommentDTO.getText());
        comment.setCreationDate(LocalDateTime.now());
        comment.setFrom(fromUser);

        return taskCommentRepository.save(comment);
    }
}
