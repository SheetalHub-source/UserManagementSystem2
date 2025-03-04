package com.example.UserManagementSystem.controller.Controller;

import com.example.UserManagementSystem.JWT.JwtUtils;
import com.example.UserManagementSystem.dto.AuthRequest;
import com.example.UserManagementSystem.dto.UserRequest;
import com.example.UserManagementSystem.dto.UserResponse;
import com.example.UserManagementSystem.resultGenericClass.GenericResponse;
import com.example.UserManagementSystem.service.UserService;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.Base64;

@Controller
public class MainController {
    @Autowired
    private UserService userService;

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
    public String homePage(){
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

}
