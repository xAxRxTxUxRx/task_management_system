package com.artur.task_management_system.service;

import com.artur.task_management_system.dto.TaskCreationDTO;
import com.artur.task_management_system.exception.EntityNotFoundByIdException;
import com.artur.task_management_system.exception.NoRightsException;
import com.artur.task_management_system.model.Task;
import com.artur.task_management_system.model.TaskComment;
import com.artur.task_management_system.model.User;
import com.artur.task_management_system.model.attributes.TaskPriority;
import com.artur.task_management_system.model.attributes.TaskStatus;
import com.artur.task_management_system.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskService taskService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @Test
    void testGetAllTasks() {
        PageRequest pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "field");
        Page<Task> tasks = new PageImpl<>(Collections.emptyList());

        when(taskRepository.findAll(pageable)).thenReturn(tasks);

        Page<Task> result = taskService.getAllTasks(0, 10, "field", "Asc");
        assertEquals(tasks, result);

        verify(taskRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetTaskById() {
        Task task = new Task();
        task.setId(1L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task result = taskService.getTaskById(1L);
        assertEquals(task, result);

        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void testGetTaskById_ThrowsEntityNotFoundByIdException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundByIdException.class, () -> taskService.getTaskById(1L));

        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAuthoredTasks() {
        PageRequest pageable = PageRequest.of(0, 10, Sort.Direction.ASC, "field");
        Page<Task> tasks = new PageImpl<>(Collections.emptyList());
        User loggedInUser = User.builder().id(1L).email("test@gmail.com").build();

        when(taskRepository.findAllByAuthorId(loggedInUser.getId(), pageable)).thenReturn(tasks);
        when(authentication.getName()).thenReturn(loggedInUser.getEmail());
        when(userService.getUserByEmail(loggedInUser.getEmail())).thenReturn(loggedInUser);
        Page<Task> result = taskService.getAuthoredTasks(0, 10, "field", "Asc");

        assertEquals(tasks, result);
        verify(taskRepository, times(1)).findAllByAuthorId(loggedInUser.getId(), pageable);
    }

    @Test
    void testDeleteTaskById() {
        Task task = new Task();
        task.setId(1L);
        User author = new User();
        author.setId(1L);
        task.setAuthor(author);

        when(authentication.getName()).thenReturn("user1@example.com");
        when(userService.getUserByEmail("user1@example.com")).thenReturn(author);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.deleteTaskById(1L);

        verify(taskRepository, times(1)).delete(task);
    }

    @Test
    void testDeleteTaskById_ThrowsNoRightsException() {
        Task task = new Task();
        task.setId(1L);
        User author = new User();
        author.setId(1L);
        task.setAuthor(author);

        User anotherUser = new User();
        anotherUser.setId(2L);

        when(authentication.getName()).thenReturn("user2@example.com");
        when(userService.getUserByEmail("user2@example.com")).thenReturn(anotherUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(NoRightsException.class, () -> taskService.deleteTaskById(1L));

        verify(taskRepository, never()).delete(task);
    }

    @Test
    void testAddTask() {
        User user = new User();
        user.setId(1L);
        TaskCreationDTO taskDTO = new TaskCreationDTO();
        taskDTO.setTitle("New Task");
        taskDTO.setDescription("Task Description");
        taskDTO.setPriority(TaskPriority.LOW);
        taskDTO.setDeadLineDate(LocalDateTime.now().plusDays(1));

        when(authentication.getName()).thenReturn("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(user);
        when(userService.getUserById(1L)).thenReturn(user);
        when(taskRepository.save(any(Task.class))).thenReturn(new Task());

        taskService.addTask(taskDTO);

        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void testUpdateTask() {
        Task task = new Task();
        task.setId(1L);
        User author = new User();
        author.setId(1L);
        task.setAuthor(author);

        TaskCreationDTO taskDTO = new TaskCreationDTO();
        taskDTO.setTitle("Updated Task");
        taskDTO.setDescription("Updated Description");

        when(authentication.getName()).thenReturn("user1@example.com");
        when(userService.getUserByEmail("user1@example.com")).thenReturn(author);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.updateTask(1L, taskDTO);

        verify(taskRepository, times(1)).save(task);
        assertEquals("Updated Task", task.getTitle());
        assertEquals("Updated Description", task.getDescription());
    }

    @Test
    void testUpdateTask_ThrowsNoRightsException() {
        Task task = new Task();
        task.setId(1L);
        User author = new User();
        author.setId(1L);
        task.setAuthor(author);

        User anotherUser = new User();
        anotherUser.setId(2L);

        TaskCreationDTO taskDTO = new TaskCreationDTO();

        when(authentication.getName()).thenReturn("user2@example.com");
        when(userService.getUserByEmail("user2@example.com")).thenReturn(anotherUser);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(NoRightsException.class, () -> taskService.updateTask(1L, taskDTO));

        verify(taskRepository, never()).save(task);
    }

    @Test
    void testUpdateTaskStatus() {
        Task task = new Task();
        task.setId(1L);
        User author = new User();
        author.setId(1L);
        task.setAuthor(author);

        when(authentication.getName()).thenReturn("user1@example.com");
        when(userService.getUserByEmail("user1@example.com")).thenReturn(author);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.updateTaskStatus(1L, TaskStatus.IN_PROCESS);

        verify(taskRepository, times(1)).save(task);
        assertEquals(TaskStatus.IN_PROCESS, task.getStatus());
    }

    @Test
    void testAssignTaskPerformer() {
        Task task = new Task();
        task.setId(1L);
        User author = new User();
        author.setId(1L);
        task.setAuthor(author);

        User performer = new User();
        performer.setId(2L);

        when(authentication.getName()).thenReturn("user1@example.com");
        when(userService.getUserByEmail("user1@example.com")).thenReturn(author);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userService.getUserById(2L)).thenReturn(performer);

        taskService.assignTaskPerformer(1L, 2L);

        verify(taskRepository, times(1)).save(task);
        assertTrue(task.getPerformers().contains(performer));
    }

    @Test
    void testCommentTask() {
        Task task = new Task();
        task.setId(1L);
        TaskComment comment = new TaskComment();
        comment.setText("Comment");

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.commentTask(1L, comment);

        verify(taskRepository, times(1)).save(task);
        assertTrue(task.getComments().contains(comment));
    }
}
