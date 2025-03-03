package com.example.UserManagementSystem.dto;
import org.springframework.web.bind.annotation.*;

/*import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;*/

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


public record UserRequest(
        Long uniqueId,
        @NotNull(message = "Username must not be null")
        @NotEmpty(message = "Username must not be empty")
        String userName,
        @NotNull(message = "Email must not be null")
        String email,
        @NotNull(message = "Password must not be null")
        @NotEmpty(message = "Password must not be empty")
        String password,
        @NotBlank(message = "Role cannot be empty.")
        String role
) {
}
