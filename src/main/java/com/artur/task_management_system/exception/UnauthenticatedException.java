package com.artur.task_management_system.exception;

public class UnauthenticatedException extends RuntimeException{
    public UnauthenticatedException(){
        super("No authenticated user");
    }
}
