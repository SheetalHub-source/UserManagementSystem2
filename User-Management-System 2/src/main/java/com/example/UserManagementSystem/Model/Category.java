package com.example.UserManagementSystem.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;


@Getter
@Setter
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, updatable = false)
    private Long uniqueId;

    @Column(unique = true,nullable = false)
    private String categoryName ;

    @Column(length = 250)
    private String categoryDesc;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "category",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Set<Product> products;

    public Category(String categoryName,  String categoryDesc) {
       this.categoryName=categoryName;
        this.categoryDesc=categoryDesc;
    }
    public Category(){}

    @PrePersist public void generateUniqueId() {
        long timestamp = Instant.now().toEpochMilli();
        this.uniqueId = (long) ((timestamp % 100000)+(Math.random()*1000));
    }

}
