package com.example.UserManagementSystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest(

        Long uniqueId,

        @NotNull(message = "Category Name should not be null")
        @NotBlank(message = "Category Name should not be empty or whitespace only")
        String categoryName,
        @NotNull(message = "Category Description should not be null")
        @NotBlank(message = "Category Description should not be empty or whitespace only")
        String categoryDesc
) {
}
