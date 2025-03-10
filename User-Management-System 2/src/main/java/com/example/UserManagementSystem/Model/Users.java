package com.example.UserManagementSystem.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(unique = true, updatable = false)
    private Long uniqueId;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;


    @Column(nullable = false)
    private String role;

    @CreationTimestamp  // Automatically sets the current timestamp when the record is created
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // Automatically update the current timestamp when the record is modify
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Users(String userName, String password, String email,String role) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public Users() {
    }
    @PrePersist public void generateUniqueId() {
        long timestamp = Instant.now().toEpochMilli();
        this.uniqueId = (long) ((timestamp % 100000)+(Math.random()*1000));
    }
}
