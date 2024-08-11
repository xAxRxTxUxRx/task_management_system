package com.artur.task_management_system.controller;

import com.artur.task_management_system.dto.TaskCommentCreationDTO;
import com.artur.task_management_system.dto.TaskCreationDTO;
import com.artur.task_management_system.model.Task;
import com.artur.task_management_system.model.TaskComment;
import com.artur.task_management_system.model.attributes.TaskStatus;
import com.artur.task_management_system.service.TaskCommentService;
import com.artur.task_management_system.service.TaskService;
import com.artur.task_management_system.dto.TaskViewDTO;
import com.artur.task_management_system.dto.mappers.TaskMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для управления задачами.
 */
@Tag(name = "Tasks")
@RestController
@AllArgsConstructor
@RequestMapping(path = "api/tasks")
public class TaskController {
    private final TaskMapper taskMapper = Mappers.getMapper(TaskMapper.class);

    private final TaskService taskService;
    private final TaskCommentService taskCommentService;

    /**
     * Получение списка всех задач.
     *
     * @param pageNumber номер страницы для пагинации
     * @param pageSize размер страницы для пагинации
     * @param field поле для сортировки
     * @param directionStr направление сортировки
     * @return список задач
     */
    @GetMapping
    @Operation(
            summary = "Get all tasks",
            description = "Retrieve a paginated/sorted list of all tasks",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of task list",
                            content = @Content(schema = @Schema(implementation = TaskViewDTO.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = Void.class)),
                            description = "Bad request. (Pagination can't be null/Wrong sorting direction value)"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = Void.class)))
            }
    )
    public ResponseEntity<List<TaskViewDTO>> getAllTasks(
            @Parameter(description = "Page number for pagination", example = "0", required = true)
            @RequestParam(value = "pageNumber")
            Integer pageNumber,

            @Parameter(description = "Size of the page for pagination", example = "10", required = true)
            @RequestParam(value = "pageSize")
            Integer pageSize,

            @Parameter(description = "Field to sort by", example = "title", required = false)
            @RequestParam(value = "field", required = false)
            String field,

            @Parameter(description = "Sorting direction ('Asc' or 'Desc')", example = "Asc", required = false)
            @RequestParam(value = "direction", required = false)
            String directionStr) {
        Page<Task> tasks = taskService.getAllTasks(pageNumber, pageSize, field, directionStr);
        List<TaskViewDTO> taskViewDTOs = tasks.stream().map(taskMapper::taskToTaskViewDTO).toList();
        return new ResponseEntity<>(taskViewDTOs, HttpStatus.OK);
    }

    /**
     * Получение списка задач, авторизованных текущим пользователем.
     *
     * @param pageNumber номер страницы для пагинации
     * @param pageSize размер страницы для пагинации
     * @param field поле для сортировки
     * @param directionStr направление сортировки
     * @return список задач
     */
    @GetMapping("/myAuthored")
    @Operation(
            summary = "Get all tasks authored by logged-in user",
            description = "Retrieve a paginated/sorted list of all tasks authored by logged-in user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of task list",
                            content = @Content(schema = @Schema(implementation = TaskViewDTO.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = Void.class)),
                            description = "Bad request. (Pagination can't be null/Wrong sorting direction value)"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = Void.class)))
            }
    )
    public ResponseEntity<List<TaskViewDTO>> getMyAuthoredTasks(
            @Parameter(description = "Page number for pagination", example = "0", required = true)
            @RequestParam(value = "pageNumber")
            Integer pageNumber,

            @Parameter(description = "Size of the page for pagination", example = "10", required = true)
            @RequestParam(value = "pageSize")
            Integer pageSize,

            @Parameter(description = "Field to sort by", example = "title", required = false)
            @RequestParam(value = "field", required = false)
            String field,

            @Parameter(description = "Sorting direction ('Asc' or 'Desc')", example = "Asc", required = false)
            @RequestParam(value = "direction", required = false)
            String directionStr) {
        Page<Task> tasks = taskService.getAuthoredTasks(pageNumber, pageSize, field, directionStr);
        List<TaskViewDTO> taskViewDTOs = tasks.stream().map(taskMapper::taskToTaskViewDTO).toList();
        return new ResponseEntity<>(taskViewDTOs, HttpStatus.OK);
    }

    /**
     * Получение списка задач, назначенных текущему пользователю.
     *
     * @param pageNumber номер страницы для пагинации
     * @param pageSize размер страницы для пагинации
     * @param field поле для сортировки
     * @param directionStr направление сортировки
     * @return список задач
     */
    @GetMapping("/myAssigned")
    @Operation(
            summary = "Get all tasks assigned to logged-in user",
            description = "Retrieve a paginated/sorted list of all tasks assign to logged-in user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of task list",
                            content = @Content(schema = @Schema(implementation = TaskViewDTO.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = Void.class)),
                            description = "Bad request. (Pagination can't be null/Wrong sorting direction value)"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = Void.class)))
            }
    )
    public ResponseEntity<List<TaskViewDTO>> getMyAssignedTasks(
            @Parameter(description = "Page number for pagination", example = "0", required = true)
            @RequestParam(value = "pageNumber")
            Integer pageNumber,

            @Parameter(description = "Size of the page for pagination", example = "10", required = true)
            @RequestParam(value = "pageSize")
            Integer pageSize,

            @Parameter(description = "Field to sort by", example = "title", required = false)
            @RequestParam(value = "field", required = false)
            String field,

            @Parameter(description = "Sorting direction ('Asc' or 'Desc')", example = "Asc", required = false)
            @RequestParam(value = "direction", required = false)
            String directionStr) {
        Page<Task> tasks = taskService.getAssignedTasks(pageNumber, pageSize, field, directionStr);
        List<TaskViewDTO> taskViewDTOs = tasks.stream().map(taskMapper::taskToTaskViewDTO).toList();
        return new ResponseEntity<>(taskViewDTOs, HttpStatus.OK);
    }

    /**
     * Получение списка задач конкретного автора.
     *
     * @param authorId идентификатор автора
     * @param pageNumber номер страницы для пагинации
     * @param pageSize размер страницы для пагинации
     * @param field поле для сортировки
     * @param directionStr направление сортировки
     * @return список задач
     */
    @GetMapping("/author/{authorId}")
    @Operation(
            summary = "Get all tasks by author id",
            description = "Retrieve a paginated/sorted list of all tasks by author id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of task list",
                            content = @Content(schema = @Schema(implementation = TaskViewDTO.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = Void.class)),
                            description = "Bad request. (Pagination can't be null/Wrong sorting direction value)"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = Void.class)))
            }
    )
    public ResponseEntity<List<TaskViewDTO>> getAllTasksByAuthorId(
            @Parameter(description = "Id of author whose tasks to get", example = "1", required = true)
            @PathVariable("authorId") Long authorId,

            @Parameter(description = "Page number for pagination", example = "0", required = true)
            @RequestParam(value = "pageNumber")
            Integer pageNumber,

            @Parameter(description = "Size of the page for pagination", example = "10", required = true)
            @RequestParam(value = "pageSize")
            Integer pageSize,

            @Parameter(description = "Field to sort by", example = "title", required = false)
            @RequestParam(value = "field", required = false)
            String field,

            @Parameter(description = "Sorting direction ('asc' or 'desc')", example = "asc", required = false)
            @RequestParam(value = "direction", required = false)
            String directionStr) {
        Page<Task> tasks = taskService.getAllTasksByAuthorId(authorId, pageNumber, pageSize, field, directionStr);
        List<TaskViewDTO> taskViewDTOs = tasks.stream().map(taskMapper::taskToTaskViewDTO).toList();
        return new ResponseEntity<>(taskViewDTOs, HttpStatus.OK);
    }


    /**
     * Получение списка задач конкретного исполнителя.
     *
     * @param performerId идентификатор исполнителя
     * @param pageNumber номер страницы для пагинации
     * @param pageSize размер страницы для пагинации
     * @param field поле для сортировки
     * @param directionStr направление сортировки
     * @return список задач
     */
    @GetMapping("/performer/{performerId}")
    @Operation(
            summary = "Get all tasks by performer id",
            description = "Retrieve a paginated/sorted list of all tasks by performer id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of task list",
                            content = @Content(schema = @Schema(implementation = TaskViewDTO.class))),
                    @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = Void.class)),
                            description = "Bad request. (Pagination can't be null/Wrong sorting direction value)"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = Void.class)))
            }
    )
    public ResponseEntity<List<TaskViewDTO>> getAllTasksByPerformId(
            @Parameter(description = "Id of performer whose task to get", example = "1", required = true)
            @PathVariable("performerId") Long performerId,

            @Parameter(description = "Page number for pagination", example = "0", required = true)
            @RequestParam(value = "pageNumber")
            Integer pageNumber,

            @Parameter(description = "Size of the page for pagination", example = "10", required = true)
            @RequestParam(value = "pageSize")
            Integer pageSize,

            @Parameter(description = "Field to sort by", example = "title", required = false)
            @RequestParam(value = "field", required = false)
            String field,

            @Parameter(description = "Sorting direction ('asc' or 'desc')", example = "asc", required = false)
            @RequestParam(value = "direction", required = false)
            String directionStr) {
        Page<Task> tasks = taskService.getAllTasksByPerformerId(performerId, pageNumber, pageSize, field, directionStr);
        List<TaskViewDTO> taskViewDTOs = tasks.stream().map(taskMapper::taskToTaskViewDTO).toList();
        return new ResponseEntity<>(taskViewDTOs, HttpStatus.OK);
    }

    /**
     * Получение задачи по идентификатору.
     *
     * @param taskId идентификатор задачи
     * @return задача
     */
    @GetMapping("/{taskId}")
    @Operation(
            summary = "Get task by id",
            description = "Fetches a task based on the provided taskId",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of task",
                            content = @Content(schema = @Schema(implementation = TaskViewDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "404", description = "Task not found",
                            content = @Content(schema = @Schema(implementation = Void.class)))
            }
    )
    public ResponseEntity<TaskViewDTO> getTaskById(
            @Parameter(description = "ID of task to retrieve", example = "1", required = true)
            @PathVariable("taskId")
            Long taskId) {
        Task task = taskService.getTaskById(taskId);
        TaskViewDTO taskViewDTO = taskMapper.taskToTaskViewDTO(task);
        return new ResponseEntity<>(taskViewDTO, HttpStatus.OK);
    }

    /**
     * Удаление задачи по идентификатору.
     *
     * @param taskId идентификатор задачи
     */
    @DeleteMapping("/{taskId}")
    @Operation(
            summary = "Delete task by id",
            description = "Deletes a task based on the provided taskId",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of task"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Task not found")
            }
    )
    public void deleteTaskById(
            @Parameter(description = "ID of task to delete", example = "1", required = true)
            @PathVariable("taskId")
            Long taskId
    ) {
        taskService.deleteTaskById(taskId);
    }

    /**
     * Создание новой задачи.
     *
     * @param taskDTO данные для создания задачи
     * @return идентификатор созданной задачи
     */
    @PostMapping
    @Operation(
            summary = "Create new task",
            description = "Creates new task of TaskCreationDTO",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Successful creation of task. Return ID of created task",
                            content = @Content(schema = @Schema(implementation = Long.class))),
                    @ApiResponse(responseCode = "400", description = "Bad request",
                            content = @Content(schema = @Schema(implementation = Void.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = Void.class)))
            }
    )
    public ResponseEntity<Long> addTask(@Valid @RequestBody TaskCreationDTO taskDTO) {
        Long task_id = taskService.addTask(taskDTO);
        return new ResponseEntity<>(task_id, HttpStatus.OK);
    }

    /**
     * Обновление существующей задачи.
     *
     * @param taskId идентификатор задачи
     * @param taskDTO данные для обновления задачи
     */
    @PutMapping("/{taskId}")
    @Operation(
            summary = "Update task by id",
            description = "Updates existing task by taskId",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of task"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Task not found")
            }
    )
    public void updateTask(
            @Parameter(description = "ID of task to update", example = "1", required = true)
            @PathVariable("taskId") Long taskId,

            @Valid @RequestBody TaskCreationDTO taskDTO) {
        taskService.updateTask(taskId, taskDTO);
    }

    /**
     * Обновление статуса задачи.
     *
     * @param taskId идентификатор задачи
     * @param status новый статус задачи
     */
    @PutMapping("/{taskId}/status")
    @Operation(
            summary = "Update status of task by id",
            description = "Updates status of task by taskId",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of status of task"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Task not found")
            }
    )
    public void updateTaskStatus(
            @Parameter(description = "ID of task which status update to", example = "1", required = true)
            @PathVariable("taskId") Long taskId,

            @Parameter(in = ParameterIn.QUERY,
                    name = "status",
                    description = "New status of the task",
                    required = true,
                    schema = @Schema(implementation = TaskStatus.class))
            @RequestParam(name = "status") TaskStatus status) {
        taskService.updateTaskStatus(taskId, status);
    }

    /**
     * Назначение задачи исполнителю.
     *
     * @param taskId идентификатор задачи
     * @param performerId идентификатор исполнителя
     */
    @PostMapping("/{taskId}/performer/{performerId}")
    @Operation(
            summary = "Assign task to performer",
            description = "Assigns task by taskId to performer by performerId",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of status of task"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Task/User not found")
            }
    )
    public void assignTaskPerformer(
            @Parameter(description = "ID of task to assign", example = "1", required = true)
            @PathVariable("taskId") Long taskId,

            @Parameter(description = "ID of user assign task to", example = "1", required = true)
            @PathVariable("performerId") Long performerId) {
        taskService.assignTaskPerformer(taskId, performerId);
    }

    /**
     * Оставление комментария к задаче.
     *
     * @param taskId идентификатор задачи
     * @param taskCommentDTO данные для комментария
     */
    @PostMapping("/{taskId}/comment")
    @Operation(
            summary = "Leave a comment to the task",
            description = "Creates comment from TaskCommentCreationDTO to the task by taskId",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of status of task"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Task not found")
            }
    )
    public void commentTask(
            @Parameter(description = "ID of task to live comment at", example = "1", required = true)
            @PathVariable("taskId") Long taskId,

            @Valid @RequestBody TaskCommentCreationDTO taskCommentDTO) {
        TaskComment taskComment = taskCommentService.addTaskComment(taskCommentDTO);
        taskService.commentTask(taskId, taskComment);
    }
}
