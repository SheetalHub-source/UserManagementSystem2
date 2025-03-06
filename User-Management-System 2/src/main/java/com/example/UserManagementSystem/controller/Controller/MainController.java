package com.example.UserManagementSystem.controller.Controller;

import com.example.UserManagementSystem.JWT.JwtUtils;
import com.example.UserManagementSystem.dto.AuthRequest;
import com.example.UserManagementSystem.dto.ProductResponse;
import com.example.UserManagementSystem.dto.UserRequest;
import com.example.UserManagementSystem.dto.UserResponse;
import com.example.UserManagementSystem.entities.Category;
import com.example.UserManagementSystem.resultGenericClass.GenericResponse;
import com.example.UserManagementSystem.service.CategoryService;
import com.example.UserManagementSystem.service.ProductService;
import com.example.UserManagementSystem.service.UserService;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

@Controller
public class MainController {
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;
    @Autowired
    private JwtUtils jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/loginpage")
    public String loginPage(){
        System.out.println("Login page controller is called ...");
        return "login";
    }
    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return ResponseEntity.ok("Token is valid");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
    }

@GetMapping("/home")
    public String homePage(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "8") Integer size,
                           @RequestParam(defaultValue = "uniqueProductId") String field,
                           @RequestParam(defaultValue = "desc") String orderBy,
                           @RequestParam(required = false) Long uniqueProductId,
                           @RequestParam(required = false) Long categoryId,
                           @RequestParam(required = false) String productName,
                           @RequestParam(required = false) String categoryName,
                           @RequestParam(required = false) String variant,
                           @RequestParam(required = false) Long minPrice,
                           @RequestParam(required = false) Long maxPrice,
                           Model model){
    Page<ProductResponse> productResponses = productService.findByCriteria(page, size, field, orderBy, uniqueProductId, categoryId, productName, categoryName, variant,minPrice,maxPrice);
    List<Category> categories = categoryService.getAllCategories();
    List<ProductResponse> products = productResponses.getContent();

    model.addAttribute("products", products);
    model.addAttribute("categories", categories);
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", productResponses.getTotalPages());
    model.addAttribute("pageSize", size);
    model.addAttribute("sortField", field);
    model.addAttribute("sortDirection", orderBy);

        return "home";
    }


    @GetMapping("/failed")
    public String loginFailPage(){
        return "loginFail";
    }

    @GetMapping("/")
    public String test() {
        System.out.println("Controller method is called!");
        return "home";
    }
    @GetMapping("/admin-dashboard")
    public String adminDash(){
        return "adminDashboard";
    }
    @GetMapping("/superadmin-dashboard")
    public String superadminDash(){
        return "Superadmin";
    }
    @GetMapping("/api/images/view/{imageName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imageName) throws IOException {
        Path imagePath = Paths.get(System.getProperty("user.dir") + "/uploads", imageName);

        if (!Files.exists(imagePath)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }

        Resource resource = new UrlResource(imagePath.toUri());
        String contentType = Files.probeContentType(imagePath);

        if (contentType == null) {
            contentType = "application/octet-stream"; // Fallback
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imageName + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }


}
