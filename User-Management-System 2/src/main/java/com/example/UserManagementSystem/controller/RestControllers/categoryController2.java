package com.example.UserManagementSystem.controller.RestControllers;

import com.example.UserManagementSystem.dto.CategoryRequest;
import com.example.UserManagementSystem.dto.CategoryResponse;
import com.example.UserManagementSystem.resultGenericClass.GenericResponse;
import com.example.UserManagementSystem.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class categoryController2 {
    @Autowired
    private CategoryService categoryService;
//-------------Get--------------

    @GetMapping
    public GenericResponse<List<CategoryResponse>> getCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "uniqueId") String field,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String categoryDesc,
            @RequestParam(required = false) String uniqueId
                                                         ){
        Page<CategoryResponse> categoryResponsePage = categoryService.findByCriteria(page,size,order,field,categoryName,categoryDesc,uniqueId);
       return  GenericResponse.success(categoryResponsePage.getContent(),"Data fetched Successfully");
    }

    //----------------Create--------------------
    @PostMapping
    public GenericResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest categoryRequest){
         CategoryResponse categoryResponse = categoryService.createOrUpdateCategory(categoryRequest);
         return GenericResponse.success(categoryResponse,"OK");
    }


    //----------------DELETE-------------------
    @DeleteMapping("/{uniqueId}")
    public GenericResponse<String> deleteCategory(@PathVariable Long uniqueId){
        String msg = categoryService.deleteCategory(uniqueId);
        return GenericResponse.success(msg,"Successfully");
    }


}
