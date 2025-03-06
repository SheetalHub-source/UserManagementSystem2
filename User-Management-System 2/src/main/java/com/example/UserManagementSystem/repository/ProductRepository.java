package com.example.UserManagementSystem.repository;

import com.example.UserManagementSystem.dto.ProductRequest;
import com.example.UserManagementSystem.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {

    Optional<Product> findByUniqueProductId(Long aLong);

    boolean existsByUniqueProductId(Long uniqueProductId);

    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.category.id = :categoryId")
    boolean existsByCategory_Id(Long categoryId);

    boolean existsByProductName(String productName);

    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.category.categoryName = :categoryName")
    boolean existsByCategory_CategoryName(String categoryName);

    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    @Query("SELECT COUNT(p) > 0 FROM Product p JOIN p.variantSet v WHERE v.optionsData LIKE %:optionsData%")
    boolean existsByVariantOptions(@Param("optionsData") String optionsData);

    @Query("SELECT MIN(p.price) FROM Product p")
    Long findMinPrice();

    // Find the maximum price from the database
    @Query("SELECT MAX(p.price) FROM Product p")
    Long findMaxPrice();

    // Find all products within the given price range
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findAllByPriceBetween(Integer minPrice, Integer maxPrice);

    @Query("SELECT p.id FROM Product p WHERE p.uniqueProductId = :uniqueProductId")
    Long findIdByUniqueProductId(@Param("uniqueProductId") Long uniqueProductId);

    Product findByProductDesc(String productDesc);

    Product findByProductName(String searchValue);
}
