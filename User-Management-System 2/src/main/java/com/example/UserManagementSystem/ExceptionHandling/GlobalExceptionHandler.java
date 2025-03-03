package com.example.UserManagementSystem.ExceptionHandling;

import com.example.UserManagementSystem.resultGenericClass.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.rmi.MarshalledObject;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(CustomValidationException e) {
        Map<String, Object> response = new HashMap<>();
        response.put("Message", "Validation Failed");
        response.put("Error", e.getErrors());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String,String>> handleResourceNotFoundException(ResourceNotFoundException  e){
        Map<String,String> res = new HashMap<>();
        res.put("error",e.getMessage());
        return new ResponseEntity<>(res,HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity <Map<String,Map<String,String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        Map<String,String> errors = new HashMap<>();

        for(FieldError error:ex.getBindingResult().getFieldErrors()){
            errors.put(error.getField(),error.getDefaultMessage());
        }
        Map<String,Map<String,String>>  response = new HashMap<>();
        response.put("message",errors);

        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity <Map<String,Map<String,String>>> handleException(Exception ex){
        Map<String,String> errors = new HashMap<>();
        errors.put("Errors", ex.getMessage());
        Map<String,Map<String,String>>  response = new HashMap<>();
        response.put("message",errors);
        ex.printStackTrace();

        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
