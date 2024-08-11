package com.artur.task_management_system.exception;

public class ConfirmationTokenNotFoundException extends RuntimeException{
    public ConfirmationTokenNotFoundException(String token){
        super(String.format("Confirmation token %s not found", token));
    }
}
