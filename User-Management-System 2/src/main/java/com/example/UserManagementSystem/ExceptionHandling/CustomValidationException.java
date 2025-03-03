package com.example.UserManagementSystem.ExceptionHandling;

import java.util.List;

public class CustomValidationException extends RuntimeException{
    private List<String> errors;

    public CustomValidationException(List<String> errors) {
        super("Validation failed");
        System.out.println("Constructor is also called of CustomValidationException");
        this.errors = errors;
        System.out.println(errors.toString());
    }

    public List<String> getErrors() {
        System.out.println("Get Error method is called ");
        return errors;
    }
}
