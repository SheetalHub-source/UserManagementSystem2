package com.example.UserManagementSystem.controller.RestControllers;

import com.example.UserManagementSystem.dto.ProductRequest;
import com.example.UserManagementSystem.dto.ProductResponse;
import com.example.UserManagementSystem.service.CategoryService;
import com.example.UserManagementSystem.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/product")
public class ProductController2 {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;


    //----------------CREATE and UPDATE-----------------
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<?> createOrUpdateProduct(
            @RequestParam String productRequestField,
            @RequestParam(value = "productImage",required = false) MultipartFile productImage,
            @RequestParam(value = "variantImage",required = false) MultipartFile[] variantImage) throws Exception {

        System.out.println("Incoming Request field "+productRequestField);
            ObjectMapper objectMapper = new ObjectMapper();
            ProductRequest productRequest = objectMapper.readValue(productRequestField, ProductRequest.class);
            System.out.println("Mapped productRequest "+productRequest);
           System.out.println(productRequest.variantSet());
        //productRequest.variantSet().
            ProductResponse productResponse = productService.createOrUpdateProductWithVariant(productRequest, productImage, variantImage);
            return ResponseEntity.status(HttpStatus.OK).body(productResponse);
    }

    //--------------Delete--------
   /* @DeleteMapping("/{uniqueProductId}/{variantIds}")
    public ResponseEntity<?> deleteProduct(@PathVariable("uniqueProductId") Long uniqueProductId,
                                           @PathVariable(value = "variantIds", required = false) String variantIds) throws Exception {
        System.out.println("Product Id "+uniqueProductId);
        System.out.println("Variant ID "+variantIds);
        if (uniqueProductId == null) {
            throw new Exception("Product ID must not be null.");
        }
        Long[] variantIdsArray = Arrays.stream(variantIds.split(","))
                .map(Long::parseLong)
                .toArray(Long[]::new);
        Map<String, Object> response = productService.deleteProductOrVariants(uniqueProductId, variantIdsArray);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
*/
    @DeleteMapping("/{uniqueProductId}")
    public ResponseEntity<Map<String, Object>> deleteProduct(
            @PathVariable("uniqueProductId") Long uniqueProductId) {

        Map<String, Object> response = productService.deleteProductOrVariants(uniqueProductId, null);

        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{uniqueProductId}/{variantIds}")
    public ResponseEntity<Map<String, Object>> deleteProductAndVariants(
            @PathVariable("uniqueProductId") Long uniqueProductId,
            @PathVariable(value = "variantIds",required = false
            ) String variantIds) {
        Long[] variantIdsArray = Arrays.stream(variantIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::parseLong)
                .toArray(Long[]::new);
        Map<String, Object> response = productService.deleteProductOrVariants(uniqueProductId, variantIdsArray);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping
    public ResponseEntity<?> readProductData(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "5") int size,
                                             @RequestParam(defaultValue = "uniqueProductId") String field,
                                             @RequestParam(defaultValue = "desc") String orderBy,
                                             @RequestParam(required = false)Long uniqueProductId,
                                             @RequestParam(required = false) Long categoryId,
                                             @RequestParam(required = false)String productName,
                                             @RequestParam(required = false) String categoryName,
                                             @RequestParam(required = false) String variant,
                                             @RequestParam(required = false) Long minPrice,
                                             @RequestParam(required = false) Long maxPrice)
    {
        Page<ProductResponse> productResponses = productService.findByCriteria(page,size,field,orderBy,uniqueProductId,categoryId,productName,categoryName,variant,minPrice,maxPrice);
        return ResponseEntity.status(HttpStatus.OK).body(productResponses.getContent());
    }
}
/*
line numbers are likely diverged try to find the current location inside Variant.hashCode()
*/