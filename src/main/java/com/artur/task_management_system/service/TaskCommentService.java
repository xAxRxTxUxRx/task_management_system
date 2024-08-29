package com.artur.task_management_system.service;

import com.artur.task_management_system.dto.TaskCommentCreationDTO;
import com.artur.task_management_system.exception.UnauthenticatedException;
import com.artur.task_management_system.model.TaskComment;

public interface TaskCommentService {
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
    TaskComment addTaskComment(TaskCommentCreationDTO taskCommentDTO);
}
