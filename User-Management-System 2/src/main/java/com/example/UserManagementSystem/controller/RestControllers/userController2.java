package com.example.UserManagementSystem.controller.RestControllers;

import com.example.UserManagementSystem.dto.UserRequest;
import com.example.UserManagementSystem.dto.UserResponse;
import com.example.UserManagementSystem.resultGenericClass.GenericResponse;
import com.example.UserManagementSystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
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


    //Create user with role
    @PostMapping
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



    @DeleteMapping("/{uniqueId}")
    public GenericResponse<String> deleteAdmin(@PathVariable Long uniqueId) {
        String msg = userService.deleteAdmin(uniqueId);
        return GenericResponse.success(msg, "Admin deleted successfully!");
    }

}
