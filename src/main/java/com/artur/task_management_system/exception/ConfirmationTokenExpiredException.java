package com.artur.task_management_system.exception;

public class ConfirmationTokenExpiredException extends RuntimeException{
    public ConfirmationTokenExpiredException(String token){
        super(String.format("Confirmation token %s already expired", token));
    }
}
