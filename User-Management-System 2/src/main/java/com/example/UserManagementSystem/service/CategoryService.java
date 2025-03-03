package com.example.UserManagementSystem.service;

import com.example.UserManagementSystem.ExceptionHandling.CustomValidationException;
import com.example.UserManagementSystem.ExceptionHandling.ResourceNotFoundException;
import com.example.UserManagementSystem.Specification.CategorySpecification;
import com.example.UserManagementSystem.dto.CategoryRequest;
import com.example.UserManagementSystem.dto.CategoryResponse;
import com.example.UserManagementSystem.dto.UserResponse;
import com.example.UserManagementSystem.entities.Category;
import com.example.UserManagementSystem.entities.Users;
import com.example.UserManagementSystem.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

   public Page<CategoryResponse> findByCriteria(int page, int size, String order, String field, String categoryName, String categoryDesc, String uniqueId) {
       List<String> errors = new ArrayList<>();



       Long uniqueIdLong = null;
       if (StringUtils.hasLength(uniqueId)) {
           try {
               uniqueIdLong = Long.valueOf(uniqueId);
               if (!categoryRepository.existsByUniqueId(uniqueIdLong)) {
                   errors.add("No Category available with this unique ID");
               }
           } catch (NumberFormatException e) {
               errors.add("Invalid unique ID format");
           }
       }


       if (StringUtils.hasLength(categoryName) && !categoryRepository.existsByCategoryName(categoryName)) {
           errors.add("No Category available with this Category Name.");
       }

       if (!order.matches("(?i)^(asc|desc)$")) {
           errors.add("Invalid order value. It must be 'asc' or 'desc' (case-insensitive).");
       }


       if (page < 0) {
           errors.add("Page number should be non-negative");
       }
       if (size < 1) {
           errors.add("Size should be greater than zero");
       }

       if (!errors.isEmpty()) {
           throw new CustomValidationException(errors);
       }

       Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order), field));
       Specification<Category> spec = Specification.where(null);

       if (StringUtils.hasLength(categoryName)) {
           spec = spec.and(CategorySpecification.hasCategoryName(categoryName));
       }
       if (StringUtils.hasLength(categoryDesc)) {
           spec = spec.and(CategorySpecification.hasCategoryDescription(categoryDesc));
       }
       if (uniqueIdLong != null) {
           spec = spec.and(CategorySpecification.hasUniqueId(uniqueIdLong));
       }

       Page<Category> categoryPage = categoryRepository.findAll(spec, pageable);
       return categoryPage.map(this::convertToResponseDto);
   }

    private CategoryResponse convertToResponseDto(Category category) {
        return new CategoryResponse(category.getUniqueId(), category.getCategoryName(),category.getCategoryDesc());
    }



    public CategoryResponse createOrUpdateCategory(CategoryRequest categoryRequest) {
        List<String> errors  = new ArrayList<>();

        if(categoryRequest.uniqueId()==null){
        if(categoryRepository.existsByCategoryName(categoryRequest.categoryName())){
            errors.add("Category with the name " + categoryRequest.categoryName() + " already exists (uniqueID:" + categoryRequest.uniqueId() + "). Please choose a different name.");
        }
            if (!errors.isEmpty()) {
                throw new CustomValidationException(errors);
            }
        Category category = new Category(categoryRequest.categoryName(),categoryRequest.categoryDesc());
        categoryRepository.save(category);
        return convertToResponseDto(category);
    }
        else{
            Category existCategory = categoryRepository.findByUniqueId(categoryRequest.uniqueId());
            if(existCategory==null){
                    errors.add("No Category available with this unique ID for updation");
                }
            if (!errors.isEmpty()) {
                throw new CustomValidationException(errors);
            }
            existCategory.setCategoryName(categoryRequest.categoryName());
            existCategory.setCategoryDesc(categoryRequest.categoryDesc());
            categoryRepository.save(existCategory);
            return convertToResponseDto(existCategory);

            }
        }

    public String deleteCategory(Long uniqueId) {
       // List<String> errors = new ArrayList<>();
        Category existCategory = categoryRepository.findByUniqueId(uniqueId);
        if(existCategory==null){
            throw new ResourceNotFoundException("No Category available with this unique ID for deletion");
        }
        categoryRepository.delete(existCategory);
        return"Category Successfully Deleted....";
    }

    public Category findById(Long categoryId) {
        if (categoryId == null) {
            throw new IllegalArgumentException("Category ID must not be null");
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));
    }


    public List<Category> getAllCategories() {
      return categoryRepository.findAll();
    }
}
