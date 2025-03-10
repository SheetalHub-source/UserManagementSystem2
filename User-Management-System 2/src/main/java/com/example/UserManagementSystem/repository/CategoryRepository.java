package com.example.UserManagementSystem.repository;

import com.example.UserManagementSystem.Model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {
    Page<Category> findAll(Specification<Category> spec, Pageable pageable);

    Category findByUniqueId(Long aLong);

    boolean existsByCategoryName(String categoryName);

     void deleteByUniqueId(Long uniqueId);

    boolean existsByUniqueId(Long uniqueIdLong);

    // Category findByPrimaryId(Long aLong);
}
