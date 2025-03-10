package com.example.UserManagementSystem.repository;

import com.example.UserManagementSystem.Model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<Users,Long>, JpaSpecificationExecutor<Users> {
    Users findByUniqueId(Long aLong);

    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);

    Optional<Users> findByEmail(String email);

    Users findByVerificationToken(String token);
}
