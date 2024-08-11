package com.artur.task_management_system.exception;

public class EmailTakenException extends RuntimeException {
    public EmailTakenException(String email){
        super(String.format("Email %s already taken", email));
    }
}
