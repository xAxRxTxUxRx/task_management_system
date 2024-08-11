package com.artur.task_management_system.model.attributes;

import lombok.Getter;

@Getter
public enum UserRole {
    USER("USER_ROLE");

    UserRole(String name){
        this.name = name;
    }

    private final String name;
}
