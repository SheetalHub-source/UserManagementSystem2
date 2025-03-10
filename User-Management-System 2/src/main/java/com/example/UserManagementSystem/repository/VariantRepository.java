package com.example.UserManagementSystem.repository;

import com.example.UserManagementSystem.Model.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VariantRepository extends JpaRepository<Variant,Long> {
    Variant findByUniqueId(Long aLong);


    void deleteByUniqueId(Long uniqueId);

    @Query("SELECT v FROM Variant v WHERE v.product.id = :productId")
    List<Variant> findByProductId(@Param("productId") Long productId);
}
