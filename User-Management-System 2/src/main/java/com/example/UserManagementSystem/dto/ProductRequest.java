package com.example.UserManagementSystem.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Set;

public record ProductRequest(Long uniqueProductId,
                             @NotNull(message = "Product name must not be null")
                             @NotEmpty(message = "Product Name nust not be empty or must not contain only whitespaces")
                             String productName,
                             @NotNull(message = "Product Descripation must not be null")
                             @NotEmpty(message = "Product Descripation nust not be empty or must not contain only whitespaces")
                             String productDesc,
                             @NotNull(message ="Product price must not be null" )
                             @Positive(message = "Product price must not be Negative")
                             Double price,
                             Long categoryId,
                             Set<VariantRequest> variantSet
) {


    }

