package com.artur.task_management_system.service;

import com.artur.task_management_system.dto.TaskCommentCreationDTO;
import com.artur.task_management_system.exception.UnauthenticatedException;
import com.artur.task_management_system.model.TaskComment;
import com.artur.task_management_system.model.User;
import com.artur.task_management_system.repository.TaskCommentRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Сервис для работы с комментариями к задачам.
 */
@Service
@AllArgsConstructor
public class TaskCommentService {
    private final TaskCommentRepository taskCommentRepository;
    private final UserService userService;

    /**
     * Добавляет комментарий к задаче.
     *
     * Метод создает новый комментарий на основе данных из DTO, устанавливает дату создания комментария и автора комментария
     * (пользователя, который сделал комментарий), и сохраняет комментарий в репозитории.
     *
     * @param taskCommentDTO объект TaskCommentCreationDTO, содержащий текст комментария
     * @return объект TaskComment, представляющий добавленный комментарий
     * @throws UnauthenticatedException если текущий пользователь не аутентифицирован
     */
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
