package com.artur.task_management_system.exception;

public class ConfirmationTokenConfirmedException extends RuntimeException{
    public ConfirmationTokenConfirmedException(String token){
        super(String.format("Confirmation token %s already confirmed", token));
    }
}
