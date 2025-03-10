package com.example.UserManagementSystem.Model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(unique = true, updatable = false)
    private Long uniqueProductId;

    @Column(nullable = false)
    private String productName;

    @Column(length = 250)
    private String productDesc;

    @Column(nullable = false)
    private Double price;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String imageData;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "categoryId", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL, orphanRemoval = true)
   private Set<Variant> variantSet =new HashSet<>();

    @PrePersist public void generateUniqueId() {
        long timestamp = Instant.now().toEpochMilli();
        this.uniqueProductId = (long) ((timestamp % 100000)+(Math.random()*1000));
    }

    public String getCategryName(){
        return category.getCategoryName();
    }

}




