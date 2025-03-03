package com.example.UserManagementSystem.resultGenericClass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class GenericResponse<T> {
    private boolean success;
    private String message;
    private T data;

    public static  <T> GenericResponse<T> success(T data,String msg){
        return GenericResponse.<T>builder()
                .message(msg)
                .data(data)
                .success(true)
                .build();
    }
    public static <T> GenericResponse<T> empty() {
        return success(null,"EMPTY");
    }

    public static <T> GenericResponse<T> error(String msg) {
        return GenericResponse.<T>builder()
                .message(msg)
                .success(false)
                .build();
    }
}
