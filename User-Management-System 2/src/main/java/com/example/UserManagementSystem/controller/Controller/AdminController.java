package com.example.UserManagementSystem.controller.Controller;

import com.example.UserManagementSystem.dto.UserRequest;
import com.example.UserManagementSystem.dto.UserResponse;
import com.example.UserManagementSystem.resultGenericClass.GenericResponse;
import com.example.UserManagementSystem.service.CategoryService;
import com.example.UserManagementSystem.service.ProductService;
import com.example.UserManagementSystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;


    @GetMapping
    public String listUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") Integer size,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "uniqueId") String field,
            @RequestParam(required = false) String uniqueId,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "ADMIN") String role,  // Default role to ADMIN
            Model model) {


        Page<UserResponse> userPage = userService.getAllUsers(page, size, order, uniqueId, userName, email, field, role);
        List<UserResponse> users = userPage.getContent();

        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("sortField", field);
        model.addAttribute("sortDirection", order);
        model.addAttribute("size",size);

        return "admin";
    }

    @DeleteMapping("/{uniqueId}")
    @ResponseBody// Correct URL pattern
    public GenericResponse<String> deleteAdmin(@PathVariable("uniqueId") Long uniqueId) {
        String msg = userService.deleteAdmin(uniqueId);
        return GenericResponse.success(msg, "Admin deleted successfully!");
    }


    @PostMapping
    @ResponseBody
    public GenericResponse<UserResponse> createAdmin( @Valid @RequestBody UserRequest userRequest){
        System.out.println("Received UserRequest: " + userRequest);
        UserResponse userResponse =  userService.createAndUpdateUser(userRequest);
        if(userRequest.uniqueId()==null) {
            return GenericResponse.success(userResponse, userRequest.role().toUpperCase() + " created successfully");
        }
        else {
            return GenericResponse.success(userResponse, userRequest.role().toUpperCase() + " updated successfully");
        }
    }
}
