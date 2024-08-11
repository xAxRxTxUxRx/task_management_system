package com.artur.task_management_system.service;

import com.artur.task_management_system.dto.TaskCommentCreationDTO;
import com.artur.task_management_system.exception.UnauthenticatedException;
import com.artur.task_management_system.model.TaskComment;
import com.artur.task_management_system.model.User;
import com.artur.task_management_system.repository.TaskCommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskCommentServiceTest {

    @Mock
    private TaskCommentRepository taskCommentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskCommentService taskCommentService;

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
    void testAddTaskCommentSuccess() {
        // Arrange
        String username = "user@example.com";
        String commentText = "This is a comment";

        TaskCommentCreationDTO taskCommentDTO = new TaskCommentCreationDTO();
        taskCommentDTO.setText(commentText);

        User user = new User();
        user.setEmail(username);

        TaskComment taskComment = new TaskComment();
        taskComment.setText(commentText);
        taskComment.setCreationDate(LocalDateTime.now());
        taskComment.setFrom(user);

        when(authentication.getName()).thenReturn(username);

        when(userService.getUserByEmail(username)).thenReturn(user);
        when(taskCommentRepository.save(any(TaskComment.class))).thenReturn(taskComment);

        TaskComment result = taskCommentService.addTaskComment(taskCommentDTO);

        assertNotNull(result);
        assertEquals(commentText, result.getText());
        assertEquals(user, result.getFrom());
        verify(taskCommentRepository, times(1)).save(any(TaskComment.class));
    }

    @Test
    void testAddTaskCommentUnauthenticated() {TaskCommentCreationDTO taskCommentDTO = new TaskCommentCreationDTO();
        taskCommentDTO.setText("This is a comment");

        when(securityContext.getAuthentication()).thenReturn(null);

        assertThrows(UnauthenticatedException.class, () -> {
            taskCommentService.addTaskComment(taskCommentDTO);
        });
    }
}
