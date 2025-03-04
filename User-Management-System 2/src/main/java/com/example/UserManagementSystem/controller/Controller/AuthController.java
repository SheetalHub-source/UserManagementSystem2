package com.example.UserManagementSystem.controller.Controller;

import com.example.UserManagementSystem.JWT.JwtUtils;
import com.example.UserManagementSystem.Security.CustomUserDetailsService;
import com.example.UserManagementSystem.dto.AuthRequest;
import com.example.UserManagementSystem.dto.UserRequest;
import com.example.UserManagementSystem.dto.UserResponse;
import com.example.UserManagementSystem.entities.Users;
import com.example.UserManagementSystem.repository.UserRepository;
import com.example.UserManagementSystem.resultGenericClass.GenericResponse;
import com.example.UserManagementSystem.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    public CustomUserDetailsService userDetailsService;


    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestParam String username,
                                                     @RequestParam String password,
                                                     HttpServletResponse response) {
        try {
           log.info("Login Controller is called ..................................");
            Users user = userService.findByEmail(username);
            String role = user.getRole();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            jwtUtil.generateTokenAndSetCookie(username,role,response);

            System.out.println("User Role: ......................." + role);
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Login Successful");
            responseBody.put("role", role);
            //responseBody.put(token)

            return ResponseEntity.ok(responseBody);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Invalid username or password"));
        }
    }

    @GetMapping("/validate-token")
    @ResponseBody  // ✅ Ensures JSON response
    public ResponseEntity<Map<String, String>> validateToken(@CookieValue(value = "jwt", required = false) String token) {
        Map<String, String> response = new HashMap<>();

        if (token == null) {
            response.put("status", "invalid");
            response.put("message", "No JWT found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            String username = jwtUtil.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(token, userDetails)) {
                response.put("status", "valid");
                response.put("message", "JWT is valid");
                response.put("username", username);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "invalid");
                response.put("message", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            response.put("status", "invalid");
            response.put("message", "Error validating token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
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