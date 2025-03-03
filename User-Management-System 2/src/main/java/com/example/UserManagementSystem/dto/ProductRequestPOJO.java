package com.example.UserManagementSystem.dto;

import com.example.UserManagementSystem.entities.Variant;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;

public class ProductRequestPOJO {

    private Long uniqueProductId;

//    @NotNull(message = "Product name must not be null")
//    @NotEmpty(message = "Product Name must not be empty or must not contain only whitespaces")
    private String productName;

   // @NotNull(message = "Product Image should not be null")
    private MultipartFile productImage;
//
//    @NotNull(message = "Product Description must not be null")
//    @NotEmpty(message = "Product Description must not be empty or must not contain only whitespaces")
    private String productDesc;

//    @NotNull(message = "Product price must not be null")
//    @Positive(message = "Product price must not be Negative")
    private Double price;

  //  @NotNull(message = "Category ID must not be null")
    private Long categoryId;

    private Set<VariantRequest> variantSet = new HashSet<>();

    // Default constructor (required by Spring for binding)
    public ProductRequestPOJO() {}

    // Getters and setters
    public Long getUniqueProductId() {
        return uniqueProductId;
    }

    public void setUniqueProductId(Long uniqueProductId) {
        this.uniqueProductId = uniqueProductId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public MultipartFile getProductImage() {
        return productImage;
    }

    public void setProductImage(MultipartFile productImage) {
        this.productImage = productImage;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Set<VariantRequest> getVariantSet() {
        return variantSet;
    }

    public void setVariantSet(Set<VariantRequest> variantSet) {
        this.variantSet = variantSet;
    }
}
