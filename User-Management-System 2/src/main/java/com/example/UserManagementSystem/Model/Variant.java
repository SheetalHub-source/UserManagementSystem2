package com.example.UserManagementSystem.Model;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class Variant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(unique = true, updatable = false)
    private Long uniqueId;

    @Lob
    @Column(columnDefinition = "JSON")
    private String optionsData;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String image;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @PrePersist public void generateUniqueId() {
        long timestamp = Instant.now().toEpochMilli();
        this.uniqueId = (long) ((timestamp % 100000)+(Math.random()*1000));
    }
}
