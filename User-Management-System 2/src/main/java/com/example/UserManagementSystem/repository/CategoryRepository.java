package com.example.UserManagementSystem.repository;

import com.example.UserManagementSystem.entities.Category;
import com.example.UserManagementSystem.entities.Product;
import com.example.UserManagementSystem.entities.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    Page<Category> findAll(Specification<Category> spec, Pageable pageable);

    Category findByUniqueId(Long aLong);

    boolean existsByCategoryName(String categoryName);

     void deleteByUniqueId(Long uniqueId);

    boolean existsByUniqueId(Long uniqueIdLong);

    // Category findByPrimaryId(Long aLong);
}
