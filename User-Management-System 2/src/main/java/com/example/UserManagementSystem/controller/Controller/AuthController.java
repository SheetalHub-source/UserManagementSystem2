package com.example.UserManagementSystem.controller.Controller;

import com.example.UserManagementSystem.JWT.JwtUtils;
import com.example.UserManagementSystem.dto.AuthRequest;
import com.example.UserManagementSystem.dto.UserRequest;
import com.example.UserManagementSystem.dto.UserResponse;
import com.example.UserManagementSystem.entities.Users;
import com.example.UserManagementSystem.resultGenericClass.GenericResponse;
import com.example.UserManagementSystem.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String username,
                                                     @RequestParam String password,
                                                     HttpServletResponse response) {
        try {
            System.out.println("Login controller is called ............");
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            // JWT Token Generate
            String token = jwtUtil.generateToken(username);

            // JWT Token Cookie Set
            Cookie jwtCookie = new Cookie("jwtToken", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false); // HTTPS use kar rahe ho to true set karein
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(60 * 60); // 1 Hour expiry

            response.addCookie(jwtCookie);

            // Response body me bhi token send karein
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Login Successful");
            responseBody.put("jwtToken", token);

            return ResponseEntity.ok(responseBody);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Invalid username or password"));
        }
    }


    @GetMapping("/logout")
        public ResponseEntity<String> logout (HttpServletResponse response){
            Cookie cookie = new Cookie("jwtToken", "");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(0); // Immediately expire
            cookie.setPath("/");
            response.addCookie(cookie);
            return ResponseEntity.ok("Logged out successfully!");
        }
    }

 /* @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        String token = jwtUtil.generateToken(request.username());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/signup")
    @ResponseBody
    public GenericResponse<UserResponse> createAdmin(@Valid @RequestBody UserRequest userRequest){
        UserResponse userResponse =  userService.createAndUpdateUser(userRequest);
        if(userRequest.uniqueId()==null) {
            return GenericResponse.success(userResponse, userRequest.role().toUpperCase() + " created successfully");
        }
        else {
            return GenericResponse.success(userResponse, userRequest.role().toUpperCase() + " updated successfully");
        }
    }*/