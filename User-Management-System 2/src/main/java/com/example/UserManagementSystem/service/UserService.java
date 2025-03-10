package com.example.UserManagementSystem.service;

import com.example.UserManagementSystem.ExceptionHandling.CustomValidationException;
import com.example.UserManagementSystem.ExceptionHandling.ResourceNotFoundException;
import com.example.UserManagementSystem.Specification.UserSpecification;
import com.example.UserManagementSystem.dto.UserRequest;
import com.example.UserManagementSystem.dto.UserResponse;
import com.example.UserManagementSystem.Model.Users;
import com.example.UserManagementSystem.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    UserSpecification userSpecification;
    @Autowired
    private EmailService emailService;

    public UserResponse createAndUpdateUser(UserRequest userRequest) throws MessagingException {

        String token = null;
        LocalDateTime expiryTime = null;
        if (userRequest.role().equalsIgnoreCase("USER")) {
            token = UUID.randomUUID().toString();
            expiryTime = LocalDateTime.now().plusHours(24);
        }
        if (userRequest.uniqueId() == null) {
            List<String> errors = new ArrayList<>();
            if (userRepo.existsByEmail(userRequest.email())) {
                errors.add("User with same email present...try another");
            }
            errors.addAll(validateUserRequest(userRequest));
            if (!errors.isEmpty()) {
                throw new CustomValidationException(errors);
            }

            Users user = new Users(
                    userRequest.userName()
                    , passwordEncoder.encode(userRequest.password())
                    , userRequest.email()
                    , userRequest.role().toUpperCase()
                    , token
                    , expiryTime
            );
            userRepo.save(user);
            if (userRequest.role().equalsIgnoreCase("USER")) {
                emailService.sendVerificationEmail(user.getEmail(), token);
            }
            return convertToDto(user);
        } else {
            Users existingUser = userRepo.findByUniqueId(userRequest.uniqueId());
            if (existingUser == null) {
                throw new ResourceNotFoundException("No account available with this unique ID");
            }

            List<String> errors = validateUserRequest(userRequest);
            if (!errors.isEmpty()) {
                throw new CustomValidationException(errors);
            }

            existingUser.setUserName(userRequest.userName());
            existingUser.setPassword(passwordEncoder.encode(userRequest.password()));
            existingUser.setEmail(userRequest.email());
            existingUser.setRole(userRequest.role().toUpperCase());
            if (userRequest.role().equalsIgnoreCase("USER")) {
                existingUser.setVerificationToken(token);
                existingUser.setTokenExpiry(expiryTime);
                existingUser.setActive(false); // Ensure they verify again after role update
            }

            userRepo.save(existingUser);
            if (userRequest.role().equalsIgnoreCase("USER")) {
                emailService.sendVerificationEmail(existingUser.getEmail(), token);
            }
            return convertToDto(existingUser);
        }
    }


    private List<String> validateUserRequest(UserRequest userRequest) {
        List<String> error = new ArrayList<>();

        if (!isValidName(userRequest.userName())) {
            error.add("Invalid username! Please use 5-15 characters, including letters, numbers, dots (.), underscores (_), or hyphens (-).");
        }
        if (!isValidPassword(userRequest.password())) {
            error.add("Invalid password! Your password must be at least 8 characters long and include at least one uppercase letter, one lowercase letter, one number, and one special character (@, #, $, etc.).");
        }
        if (!userRequest.email().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            error.add("Invalid email! Please enter a valid email address in the format: username@domain.com.");
        }
        if (!userRequest.role().matches("(?i)^(USER|ADMIN|SUPERADMIN)$")) {
            error.add("Invalid role! Allowed values: USER, ADMIN, SUPERADMIN.");
        }
        return error;
    }

    private static boolean isValidName(String userName) {
        String regex = "^[a-zA-Z0-9._-]{5,15}$";  // Allowed: letters, numbers, ., _, -, length: 5-15
        return userName.matches(regex);
    }

    private static boolean isValidPassword(String password) {
        String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return password.matches(regex);
    }

    private UserResponse convertToDto(Users user) {
        return new UserResponse(user.getUniqueId(), user.getUserName(), user.getEmail(), user.getPassword());
    }

    public Page<UserResponse> getAllUsers(int page, int size, String order, String uniqueId, String userName, String email, String field, String role) {
        List<String> errors = new ArrayList<>();

        // Validate Unique ID
        if (uniqueId != null) {
            try {
                Users existingUser = userRepo.findByUniqueId(Long.valueOf(uniqueId));
                if (existingUser == null) {
                    throw new ResourceNotFoundException("No account available with this unique ID");
                }
            } catch (NumberFormatException e) {
                errors.add("Invalid Unique ID! It must be a valid number.");
            }
        }

        // Validate Username
        if (userName != null && !isValidName(userName)) {
            errors.add("Invalid username! Please use 5-15 characters, including letters, numbers, dots (.), underscores (_), or hyphens (-).");
        }

        // Validate Email
        if (email != null && !email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
            errors.add("Invalid email! Please enter a valid email address in the format: username@domain.com.");
        }

        // Validate Role (Fixing reversed condition)
        if (role != null && !role.matches("(?i)^(USER|ADMIN|SUPERADMIN)$")) {
            errors.add("Invalid role! Allowed values: USER, ADMIN, SUPERADMIN.");
        }

        // Validate Order
        if (!order.matches("(?i)^(asc|desc)$")) {
            errors.add("Invalid order value. It must be 'asc' or 'desc' (case-insensitive).");
        }

        // Validate Page and Size
        if (page < 0) {
            errors.add("Page number should be positive.");
        }
        if (size < 1) {
            errors.add("Size number should be positive.");
        }

        // Throw validation errors immediately
        if (!errors.isEmpty()) {
            throw new CustomValidationException(errors);
        }

        // Sorting (Ensure case consistency)
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(order.toUpperCase()), field));

        Specification<Users> spec = Specification.where(null);

        // Apply filters
        if (StringUtils.hasLength(uniqueId)) {
            spec = spec.and(userSpecification.hasUniqueId(Long.valueOf(uniqueId)));
        }
        if (StringUtils.hasLength(userName)) {
            spec = spec.and(userSpecification.hasUserName(userName));
        }
        if (StringUtils.hasLength(email)) {
            spec = spec.and(userSpecification.hasEmail(email));
        }
        if (StringUtils.hasLength(role)) {
            spec = spec.and(userSpecification.hasRole(role));
        }

        Page<Users> usersPage = userRepo.findAll(spec, pageable);
        return usersPage.map(this::convertToDto);
    }

    public String deleteAdmin(Long uniqueId) {
        Users user = userRepo.findByUniqueId(uniqueId);
        if (user == null) {
            throw new ResourceNotFoundException("Admin not available with this uniqueId: " + uniqueId);
        }
        userRepo.delete(user);
        return "Admin deleted successfully.";
    }

    public Users findByEmail(String email) {
        return userRepo.findByEmail(email).get();
    }

    public String verifyUser(String token) {
        Users user = userRepo.findByVerificationToken(token); // Find user by token

        if (user == null) {
            return "Invalid or expired token!";
        }
        if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            return "Token expired! Request a new one.";
        }

        user.setActive(true);
        user.setVerificationToken(null);
        user.setTokenExpiry(null);
        userRepo.save(user);

        return "Email verified successfully! You can now log in.";
    }

    public String resendVerification(String email) throws MessagingException {
        Users user = userRepo.findByEmail(email).get();

        if (user == null) {
            return "User not found!";
        }
        if (user.isActive()) {
            return "User is already verified.";
        }

        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusHours(24));

        userRepo.save(user);
        emailService.sendVerificationEmail(user.getEmail(), token);
        return "Verification email is sent again";
    }

}

