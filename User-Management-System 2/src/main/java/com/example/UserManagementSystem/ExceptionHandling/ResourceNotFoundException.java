package com.example.UserManagementSystem.ExceptionHandling;

public class ResourceNotFoundException extends  RuntimeException{
    public ResourceNotFoundException(String msg){
        super(msg);
    }
}
