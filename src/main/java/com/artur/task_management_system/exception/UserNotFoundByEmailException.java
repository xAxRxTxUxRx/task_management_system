package com.artur.task_management_system.exception;

public class UserNotFoundByEmailException extends RuntimeException{
    public UserNotFoundByEmailException(String email){
        super(String.format("User not found by email %s", email));
    }
}
