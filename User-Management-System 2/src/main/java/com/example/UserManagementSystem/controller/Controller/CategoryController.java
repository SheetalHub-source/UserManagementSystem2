package com.example.UserManagementSystem.controller;

import com.example.UserManagementSystem.dto.CategoryRequest;
import com.example.UserManagementSystem.dto.CategoryResponse;
import com.example.UserManagementSystem.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Display categories
    @GetMapping
    public String getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "uniqueId") String field,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String categoryDesc,
            @RequestParam(required = false) String uniqueId,
            Model model
    ) {
        Page<CategoryResponse> categoryResponsePage = categoryService.findByCriteria(page, size, order, field, categoryName, categoryDesc, uniqueId);
        List<CategoryResponse> categoryResponses = categoryResponsePage.getContent();
        model.addAttribute("categories", categoryResponses);
        model.addAttribute("category", new CategoryRequest(null, "", "")); // Ensuring form is empty for new entry
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoryResponsePage.getTotalPages());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", field);
        model.addAttribute("sortDirection", order);
        return "category";
    }

    // Create or Update Category
    @PostMapping("/save")
    public String createCategory(@Valid @ModelAttribute("category") CategoryRequest categoryRequest) {
        categoryService.createOrUpdateCategory(categoryRequest);
        return "redirect:/category";
    }

    // Delete Category
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return "redirect:/category";
    }

    // Edit Category (Load category data into form)
    @GetMapping("/edit/{id}")
    public String editCategory(@PathVariable Long id, Model model) {
        CategoryResponse categoryResponse = categoryService.getCategoryById(id);
        model.addAttribute("category", categoryResponse);
        model.addAttribute("categories", categoryService.findByCriteria(0, 5, "desc", "uniqueId", null, null, null).getContent());
        return "category"; // Thymeleaf template
    }
}
