package com.example.UserManagementSystem.controller.RestControllers;

import com.example.UserManagementSystem.Model.Users;
import com.example.UserManagementSystem.dto.UserRequest;
import com.example.UserManagementSystem.dto.UserResponse;
import com.example.UserManagementSystem.resultGenericClass.GenericResponse;
import com.example.UserManagementSystem.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/api/users")
@Slf4j
public class userController2 {
    @Autowired
    private UserService userService;


    @GetMapping
    public GenericResponse<List<UserResponse>> fetchAdmin(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "5") int size,
                                                          @RequestParam(defaultValue = "desc") String order,

                                                          @RequestParam(defaultValue = "uniqueId") String field,
                                                          @RequestParam(required = false) String uniqueId,
                                                          @RequestParam(required = false) String userName,
                                                          @RequestParam(required = false) String email,
                                                          @RequestParam(required = false) String role
    ){

        Page<UserResponse> userResponses= userService.getAllUsers(page,size,order,uniqueId,userName,email,field,role);
        return GenericResponse.success(userResponses.getContent(),"Data fetched Successfully");
    }
    @PostMapping("/signup")
    @ResponseBody  // Ensures JSON response instead of HTML redirect
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody UserRequest userRequest) throws MessagingException {
        log.info("Incoming user Request: " + userRequest);

        userService.createAndUpdateUser(userRequest); // Process user signup

        Map<String, String> response = new HashMap<>();
        response.put("message", "Account created successfully! Check your email to verify your account.");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerificationEmail(@RequestParam("email") String email) throws MessagingException {
        String message = userService.resendVerification(email);
        return ResponseEntity.ok("New verification email sent!");
    }





    @DeleteMapping("/{uniqueId}")
    public GenericResponse<String> deleteAdmin(@PathVariable Long uniqueId) {
        String msg = userService.deleteAdmin(uniqueId);
        return GenericResponse.success(msg, "Admin deleted successfully!");
    }

}
