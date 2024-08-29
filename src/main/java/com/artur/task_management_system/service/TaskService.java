package com.artur.task_management_system.service;

import com.artur.task_management_system.dto.TaskCreationDTO;
import com.artur.task_management_system.exception.EntityNotFoundByIdException;
import com.artur.task_management_system.exception.NoRightsException;
import com.artur.task_management_system.model.Task;
import com.artur.task_management_system.model.TaskComment;
import com.artur.task_management_system.model.attributes.TaskStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;

public interface TaskService {
    /**
     * Возвращает страницу задач с учетом пагинации и сортировки.
     *
     * @param pageNumber номер страницы для пагинации
     * @param pageSize количество элементов на странице
     * @param field поле для сортировки
     * @param directionStr направление сортировки ("Asc" или "Desc")
     * @return объект Page, содержащий задачи
     */
    Page<Task> getAllTasks(Integer pageNumber, Integer pageSize,
                                  String field, String directionStr);

    /**
     * Возвращает задачу по ее идентификатору.
     *
     * @param taskId идентификатор задачи
     * @return объект задачи
     * @throws EntityNotFoundByIdException если задача не найдена
     */
    Task getTaskById(Long taskId);

    /**
     * Удаляет задачу по ее идентификатору.
     *
     * @param taskId идентификатор задачи
     * @throws NoRightsException если текущий пользователь не имеет прав на удаление задачи
     */
    @Transactional
    void deleteTaskById(Long taskId);

    /**
     * Создает новую задачу на основе данных из DTO.
     *
     * @param taskDTO объект DTO, содержащий данные для создания задачи
     * @return идентификатор созданной задачи
     */
    @Transactional
    Long addTask(TaskCreationDTO taskDTO);

    /**
     * Обновляет существующую задачу на основе данных из DTO.
     *
     * @param taskId идентификатор обновляемой задачи
     * @param taskDTO объект DTO с новыми данными для задачи
     * @throws NoRightsException если текущий пользователь не имеет прав на обновление задачи
     */
    void updateTask(Long taskId, TaskCreationDTO taskDTO);

    /**
     * Возвращает страницу задач, созданных текущим пользователем, с учетом пагинации и сортировки.
     *
     * @param pageNumber номер страницы для пагинации
     * @param pageSize количество элементов на странице
     * @param field поле для сортировки
     * @param directionStr направление сортировки ("Asc" или "Desc")
     * @return объект Page, содержащий задачи
     */
    Page<Task> getAuthoredTasks(Integer pageNumber, Integer pageSize,
                                       String field, String directionStr);

    /**
     * Возвращает страницу задач, назначенных текущему пользователю, с учетом пагинации и сортировки.
     *
     * @param pageNumber номер страницы для пагинации
     * @param pageSize количество элементов на странице
     * @param field поле для сортировки
     * @param directionStr направление сортировки ("Asc" или "Desc")
     * @return объект Page, содержащий задачи
     */
    Page<Task> getAssignedTasks(Integer pageNumber, Integer pageSize,
                                       String field, String directionStr);

    /**
     * Возвращает страницу задач определенного автора с учетом пагинации и сортировки.
     *
     * @param authorId идентификатор автора задач
     * @param pageNumber номер страницы для пагинации
     * @param pageSize количество элементов на странице
     * @param field поле для сортировки
     * @param directionStr направление сортировки ("Asc" или "Desc")
     * @return объект Page, содержащий задачи
     */
    Page<Task> getAllTasksByAuthorId(Long authorId,
                                            Integer pageNumber, Integer pageSize,
                                            String field, String directionStr);

    /**
     * Возвращает страницу задач, назначенных определенному исполнителю, с учетом пагинации и сортировки.
     *
     * @param performerId идентификатор исполнителя задач
     * @param pageNumber номер страницы для пагинации
     * @param pageSize количество элементов на странице
     * @param field поле для сортировки
     * @param directionStr направление сортировки ("Asc" или "Desc")
     * @return объект Page, содержащий задачи
     */
    Page<Task> getAllTasksByPerformerId(Long performerId,
                                               Integer pageNumber, Integer pageSize,
                                               String field, String directionStr);

    /**
     * Обновляет статус задачи.
     *
     * @param taskId идентификатор задачи
     * @param status новый статус задачи
     * @throws NoRightsException если текущий пользователь не имеет прав на обновление статуса задачи
     */
    void updateTaskStatus(Long taskId, TaskStatus status);

    /**
     * Назначает исполнителя для задачи.
     *
     * @param taskId идентификатор задачи
     * @param performerId идентификатор исполнителя
     * @throws NoRightsException если текущий пользователь не имеет прав на назначение исполнителя
     */
    @Transactional
    void assignTaskPerformer(Long taskId, Long performerId);

    /**
     * Добавляет комментарий к задаче.
     *
     * @param taskId идентификатор задачи
     * @param taskComment объект комментария
     */
    void commentTask(Long taskId, TaskComment taskComment);
}
