package com.example.UserManagementSystem.dto;

public record UserResponse(
        Long uniqueId,
        String userName,
        String email,
        String password
) {
}
