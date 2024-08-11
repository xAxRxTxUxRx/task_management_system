package com.artur.task_management_system.exception;

public class UserNotEnabledException extends RuntimeException{
    public UserNotEnabledException(){
        super("User is disabled");
    }
}
