package com.artur.task_management_system.controller;

import com.artur.task_management_system.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import lombok.AllArgsConstructor;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Контроллер для отслеживания исключений.
 */
@ControllerAdvice
@AllArgsConstructor
public class ExceptionController {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnhandled(Exception exc) {
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> handleIllegalStateException(IllegalStateException exc) {
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotEnabledException.class)
    public ResponseEntity<?> handleUserNotEnabledException(UserNotEnabledException exc){
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EmailTakenException.class)
    public ResponseEntity<?> handleEmailTakenException(EmailTakenException exc) {
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({NoRightsException.class})
    public ResponseEntity<?> handleNoRightsException(NoRightsException exc) {
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException exc){
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException exc){
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<?> handlePropertyReferenceException(PropertyReferenceException exc){
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UnauthenticatedException.class})
    public ResponseEntity<?> handleUnauthenticatedException(UnauthenticatedException exc){
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException exc) {
        List<String> errors = exc.getBindingResult().getFieldErrors()
                .stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(value
            = { EntityNotFoundByIdException.class, UserNotFoundByEmailException.class,
            ConfirmationTokenNotFoundException.class})
    public ResponseEntity<?> handleEntityNotFoundByIdException(Exception exc) {
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value
            = { ConfirmationTokenConfirmedException.class, ConfirmationTokenExpiredException.class})
    public ResponseEntity<?> handleConfirmationTokenExceptions(Exception exc) {
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {AuthenticationException.class, AuthorizationDeniedException.class})
    public ResponseEntity<?> handleAuthenticationException(Exception exc) {
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<?> handleHttpMessageNotReadableException(Exception exc){
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
