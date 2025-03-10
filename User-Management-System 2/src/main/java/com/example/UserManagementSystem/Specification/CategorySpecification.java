package com.example.UserManagementSystem.Specification;

import com.example.UserManagementSystem.Model.Category;
import org.springframework.data.jpa.domain.Specification;

public class CategorySpecification {
    public static Specification<Category> hasCategoryName(String categoryName){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("categoryName"),categoryName));
    }
    public static Specification<Category> hasCategoryDescription(String categoryDesc){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("categoryDesc"),categoryDesc));
    }
    public static Specification<Category> hasUniqueId(Long uniqueId){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("uniqueId"),uniqueId));
    }
}
