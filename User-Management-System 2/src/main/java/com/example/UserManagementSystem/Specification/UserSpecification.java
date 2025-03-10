package com.example.UserManagementSystem.Specification;

import com.example.UserManagementSystem.Model.Users;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<Users> hasUserName(String userName){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userName"),userName));
    }
    public static Specification<Users> hasEmail(String email){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("email"),email));
    }
    public static Specification<Users> hasUniqueId(Long uniqueId){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("uniqueId"),uniqueId));
    }
    public static Specification<Users> hasRole(String role){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("role"),role));
    }
}
