package com.example.UserManagementSystem.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record ProductResponse(
        Long id,
        Long uniqueProductId,
        String productName,
        String productDesc,
        Double price,
        String productImage,
        String categoryName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Set<VariantResponse> variantSet) {
}
