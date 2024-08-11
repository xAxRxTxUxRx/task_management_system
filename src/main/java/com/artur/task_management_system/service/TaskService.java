package com.artur.task_management_system.service;

import com.artur.task_management_system.dto.TaskCreationDTO;
import com.artur.task_management_system.exception.EntityNotFoundByIdException;
import com.artur.task_management_system.exception.NoRightsException;
import com.artur.task_management_system.exception.UnauthenticatedException;
import com.artur.task_management_system.model.Task;
import com.artur.task_management_system.model.TaskComment;
import com.artur.task_management_system.model.User;
import com.artur.task_management_system.model.attributes.TaskStatus;
import com.artur.task_management_system.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис для работы с Task.
 */
@Service
@AllArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;

    /**
     * Возвращает страницу задач с учетом пагинации и сортировки.
     *
     * @param pageNumber номер страницы для пагинации
     * @param pageSize количество элементов на странице
     * @param field поле для сортировки
     * @param directionStr направление сортировки ("Asc" или "Desc")
     * @return объект Page, содержащий задачи
     */
    public Page<Task> getAllTasks(Integer pageNumber, Integer pageSize,
                                  String field, String directionStr) {
        Pageable pageable = makePageable(pageNumber, pageSize, field, directionStr);
        return taskRepository.findAll(pageable);
    }

    /**
     * Возвращает задачу по ее идентификатору.
     *
     * @param taskId идентификатор задачи
     * @return объект задачи
     * @throws EntityNotFoundByIdException если задача не найдена
     */
    public Task getTaskById(Long taskId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        return taskOptional.orElseThrow(() -> new EntityNotFoundByIdException("task", taskId));
    }

    /**
     * Удаляет задачу по ее идентификатору.
     *
     * @param taskId идентификатор задачи
     * @throws NoRightsException если текущий пользователь не имеет прав на удаление задачи
     */
    @Transactional
    public void deleteTaskById(Long taskId) {
        User loggedInUser = getLoggedInUser();
        Task task = getTaskById(taskId);
        if (!Objects.equals(task.getAuthor().getId(), loggedInUser.getId())){
            throw new NoRightsException("You have no rights to delete not yours task");
        }

        for (User performer : task.getPerformers()){
            performer.removeAssignedTask(task);
        }
        taskRepository.delete(task);
    }

    /**
     * Создает новую задачу на основе данных из DTO.
     *
     * @param taskDTO объект DTO, содержащий данные для создания задачи
     * @return идентификатор созданной задачи
     */
    @Transactional
    public Long addTask(TaskCreationDTO taskDTO) {
        User loggedInUser = getLoggedInUser();
        Task task = new Task();
        copyTaskFromDTO(task, taskDTO);
        User author = userService.getUserById(loggedInUser.getId());
        task.setAuthor(author);
        taskRepository.save(task);
        task.getAuthor().addCreatedTask(task);
        return task.getId();
    }

    /**
     * Обновляет существующую задачу на основе данных из DTO.
     *
     * @param taskId идентификатор обновляемой задачи
     * @param taskDTO объект DTO с новыми данными для задачи
     * @throws NoRightsException если текущий пользователь не имеет прав на обновление задачи
     */
    public void updateTask(Long taskId, TaskCreationDTO taskDTO) {
        User loggedInUser = getLoggedInUser();
        Task task = getTaskById(taskId);
        if (!Objects.equals(task.getAuthor().getId(), loggedInUser.getId())){
            throw new NoRightsException("You have no rights to update not yours task");
        }

        copyTaskFromDTO(task, taskDTO);
        taskRepository.save(task);
    }

    /**
     * Возвращает страницу задач, созданных текущим пользователем, с учетом пагинации и сортировки.
     *
     * @param pageNumber номер страницы для пагинации
     * @param pageSize количество элементов на странице
     * @param field поле для сортировки
     * @param directionStr направление сортировки ("Asc" или "Desc")
     * @return объект Page, содержащий задачи
     */
    public Page<Task> getAuthoredTasks(Integer pageNumber, Integer pageSize,
                                       String field, String directionStr) {
        User loggedInUser = getLoggedInUser();
        return getAllTasksByAuthorId(loggedInUser.getId(), pageNumber, pageSize, field, directionStr);
    }

    /**
     * Возвращает страницу задач, назначенных текущему пользователю, с учетом пагинации и сортировки.
     *
     * @param pageNumber номер страницы для пагинации
     * @param pageSize количество элементов на странице
     * @param field поле для сортировки
     * @param directionStr направление сортировки ("Asc" или "Desc")
     * @return объект Page, содержащий задачи
     */
    public Page<Task> getAssignedTasks(Integer pageNumber, Integer pageSize,
                                       String field, String directionStr) {
        User loggedInUser = getLoggedInUser();
        return getAllTasksByPerformerId(loggedInUser.getId(), pageNumber, pageSize, field, directionStr);
    }

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
    public Page<Task> getAllTasksByAuthorId(Long authorId,
                                            Integer pageNumber, Integer pageSize,
                                            String field, String directionStr) {
        Pageable pageable = makePageable(pageNumber, pageSize, field, directionStr);
        return taskRepository.findAllByAuthorId(authorId, pageable);
    }

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
    public Page<Task> getAllTasksByPerformerId(Long performerId,
                                               Integer pageNumber, Integer pageSize,
                                               String field, String directionStr) {
        Pageable pageable = makePageable(pageNumber, pageSize, field, directionStr);
        return taskRepository.findAllByPerformerId(performerId, pageable);
    }

    /**
     * Обновляет статус задачи.
     *
     * @param taskId идентификатор задачи
     * @param status новый статус задачи
     * @throws NoRightsException если текущий пользователь не имеет прав на обновление статуса задачи
     */
    public void updateTaskStatus(Long taskId, TaskStatus status) {
        User loggedInUser = getLoggedInUser();
        Task task = getTaskById(taskId);
        if (!Objects.equals(task.getAuthor().getId(), loggedInUser.getId()) && !task.isAssignedTo(loggedInUser.getId())){
            throw new NoRightsException("You have no rights to update task status");
        }

        task.setStatus(status);
        taskRepository.save(task);
    }

    /**
     * Назначает исполнителя для задачи.
     *
     * @param taskId идентификатор задачи
     * @param performerId идентификатор исполнителя
     * @throws NoRightsException если текущий пользователь не имеет прав на назначение исполнителя
     */
    @Transactional
    public void assignTaskPerformer(Long taskId, Long performerId) {
        User loggedInUser = getLoggedInUser();
        Task task = getTaskById(taskId);
        if (!Objects.equals(task.getAuthor().getId(), loggedInUser.getId()) && !task.isAssignedTo(loggedInUser.getId())){
            throw new NoRightsException("You have no rights to assign performer to not yours tasks");
        }

        User performer = userService.getUserById(performerId);
        task.addPerformer(performer);
        taskRepository.save(task);
        performer.addAssignedTask(task);
    }

    /**
     * Добавляет комментарий к задаче.
     *
     * @param taskId идентификатор задачи
     * @param taskComment объект комментария
     */
    public void commentTask(Long taskId, TaskComment taskComment) {
        Task task = getTaskById(taskId);
        task.addComment(taskComment);
        taskRepository.save(task);
    }

    /**
     * Копирует данные из DTO в объект задачи.
     *
     * @param task объект задачи, в который будут скопированы данные
     * @param taskDTO объект DTO, содержащий данные для копирования
     */
    private void copyTaskFromDTO(Task task, TaskCreationDTO taskDTO) {
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setPriority(taskDTO.getPriority());
        task.setCreationDate(LocalDateTime.now());
        task.setStatus(TaskStatus.NEW);
        task.setDeadLineDate(taskDTO.getDeadLineDate());

        Set<User> performers = taskDTO.getPerformersIds().stream()
                .map(userService::getUserById)
                .collect(Collectors.toSet());

        task.setPerformers(performers);
    }

    /**
     * Создает объект Pageable на основе предоставленных параметров пагинации и сортировки.
     *
     * @param pageNumber номер страницы для пагинации
     * @param pageSize количество элементов на странице
     * @param field поле для сортировки
     * @param directionStr направление сортировки ("Asc" или "Desc")
     * @return объект Pageable, готовый к использованию в запросах к репозиторию
     * @throws IllegalStateException если параметры пагинации или сортировки некорректны
     */
    private Pageable makePageable(Integer pageNumber, Integer pageSize,
                                  String field, String directionStr) {
        if (pageNumber == null || pageSize == null){
            throw new IllegalStateException("Pagination cannot be null");
        }

        Pageable pageable;
        if (field != null && directionStr != null) {
            Sort.Direction direction;
            if (directionStr.equals("Asc")){
                direction = Sort.Direction.ASC;
            }else if(directionStr.equals("Desc")){
                direction = Sort.Direction.DESC;
            }else{
                throw new IllegalStateException("Wrong sorting direction value");
            }
            pageable = PageRequest.of(pageNumber, pageSize, direction, field);
        } else {
            pageable = PageRequest.of(pageNumber, pageSize);
        }
        return pageable;
    }

    /**
     * Возвращает текущего аутентифицированного пользователя.
     *
     * @return объект User, представляющий текущего аутентифицированного пользователя
     * @throws UnauthenticatedException если пользователь не аутентифицирован
     */
    private User getLoggedInUser(){
        if (SecurityContextHolder.getContext().getAuthentication() == null){
            throw new UnauthenticatedException();
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getUserByEmail(username);
    }
}
