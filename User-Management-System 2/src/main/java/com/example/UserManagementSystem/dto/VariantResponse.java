package com.example.UserManagementSystem.dto;

import java.util.List;

public record VariantResponse(
        Long uniqueId,
        String  attribute,
        String imageData) {
}
