package com.example.UserManagementSystem.controller.Controller;

import com.example.UserManagementSystem.JWT.JwtUtils;
import com.example.UserManagementSystem.Security.CustomUserDetailsService;
import com.example.UserManagementSystem.Model.Users;
import com.example.UserManagementSystem.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
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
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "User not found"));
            }

            if (!user.isActive() && user.getRole().equalsIgnoreCase("USER")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "Please verify your email before logging in."));
            }

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

            String role = user.getRole();
            String token = jwtUtil.generateTokenAndSetCookie(username, role, response);

            log.info("User Role: {}", role);

            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Login Successful");
            responseBody.put("role", role);
            responseBody.put("jwt", token);

            return ResponseEntity.ok(responseBody);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "Invalid username or password"));
        }
    }


    @GetMapping("/verify")
    public String isverifyUser(@RequestParam("token") String token) {
        String message = userService.verifyUser(token);
        return "login";
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

