package com.artur.task_management_system.exception;

public class EntityNotFoundByIdException extends RuntimeException{

    public EntityNotFoundByIdException(String entity, Long id){
        super(String.format("%s not found by id %d", entity, id));
    }
}
