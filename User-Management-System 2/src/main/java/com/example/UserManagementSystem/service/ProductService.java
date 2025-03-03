package com.example.UserManagementSystem.service;

import com.example.UserManagementSystem.ExceptionHandling.CustomValidationException;
import com.example.UserManagementSystem.ExceptionHandling.ResourceNotFoundException;
import com.example.UserManagementSystem.Specification.CategorySpecification;
import com.example.UserManagementSystem.Specification.ProductSpecification;
import com.example.UserManagementSystem.dto.*;
import com.example.UserManagementSystem.entities.Category;
import com.example.UserManagementSystem.entities.Product;
import com.example.UserManagementSystem.entities.Variant;
import com.example.UserManagementSystem.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private VariantService variantService;
    @Autowired
    private CategoryService categoryService;

//    private String encodeImageToBase64(MultipartFile image) throws IOException {
//        byte[] imageBytes = image.getBytes();
//        return Base64.getEncoder().encodeToString(imageBytes);  // Correct usage
//


    public Long findProductIdByUniqueProductId(Long uniqueProductId){
       return productRepository.findIdByUniqueProductId(uniqueProductId);
    }

    public ProductResponse findByUniqueProductId(Long uniqueProductId){
        Product product=  productRepository.findByUniqueProductId(uniqueProductId).get();
        return convertToProductResponse(product);
    }
    private String saveImage(MultipartFile image) throws IOException {
         final String UPLOAD_DIR = System.getProperty("user.dir")+"/uploads";
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) uploadDir.mkdirs();

        String uniqueFileName = UUID.randomUUID() + "_" + image.getOriginalFilename().replaceAll("\\s", "");
        String filePath = Paths.get(UPLOAD_DIR, uniqueFileName).toString();
        image.transferTo(new File(filePath));

            String imageUrl = "http://localhost:8080/api/images/view/" + uniqueFileName;
            return imageUrl;
    }


    public ProductResponse
    createOrUpdateProductWithVariant(ProductRequest productRequest, MultipartFile productImage, MultipartFile[] variantImage) throws Exception {

        List<String> error = new ArrayList<>();

        if (variantImage == null && productRequest.variantSet() != null && !productRequest.variantSet().isEmpty()) {
            error.add("Missing Image field for variant Image. There should be " + productRequest.variantSet().size() + " Variant Images");
        } else if (variantImage != null && productRequest.variantSet() != null &&
                variantImage.length < productRequest.variantSet().size()) {
            error.add("Missing Image field for variant Image. There should be " + productRequest.variantSet().size() + " Variant Images");
        }

        if (productRequest.productName() == null || productRequest.productName().isEmpty()) {
            error.add("Product Name must not be null or empty.");
        }
        if (productRequest.productDesc() == null || productRequest.productDesc().isEmpty()) {
            error.add("Product Description must not be null or empty.");
        }
        if (productRequest.price() < 0.0) {
            error.add("Product Price must be positive.");
        }


        if (!error.isEmpty()) {
            throw new CustomValidationException(error);
        }
        if (!error.isEmpty()) {
            System.out.println("Error is called");
            throw new CustomValidationException(error);
        }

//        String imageBase64 = encodeImageToBase64(productImage);
        String imageUrl = saveImage(productImage);

        Product product = (productRequest.uniqueProductId() == null) ? new Product() :
                productRepository.findByUniqueProductId(productRequest.uniqueProductId()).get();

        if (product == null && productRequest.uniqueProductId() != null) {
            throw new ResourceNotFoundException("Product with ID " + productRequest.uniqueProductId() + " not found.");
        }

        product.setProductName(productRequest.productName());
        product.setProductDesc(productRequest.productDesc());
        product.setImageData(imageUrl);
        product.setPrice(productRequest.price());

        Category category = categoryService.findById(productRequest.categoryId());
        if (category != null) {
            product.setCategory(category);
        } else {
            throw new ResourceNotFoundException("Category not found for ID: " + productRequest.categoryId());
        }
        Set<VariantRequest> variantSet = productRequest.variantSet();
        Product savedProduct = productRepository.save(product);
        Set<VariantResponse> variantResponses = new HashSet<>();
        if (variantSet != null) {
            Iterator<VariantRequest> variantIterator = variantSet.iterator();
            for (int count = 0; variantIterator.hasNext(); count++) {
                VariantRequest variantRequest = variantIterator.next();
                if (count < variantImage.length) {
                    variantResponses.add(variantService.createAndUpdateVariant(variantRequest, variantImage[count], product));
                }
            }
            ObjectMapper objectMapper = new ObjectMapper();
            Set<Variant> variants = variantResponses.stream()
                    .map(variantResponse -> {
                        Variant variant = new Variant();
                        variant.setUniqueId(variantResponse.uniqueId());

                        try {
                            // ✅ Convert optionsData (list) to JSON string
                            String optionsDataJson = objectMapper.writeValueAsString(variantResponse.attribute());
                            variant.setOptionsData(optionsDataJson);
                        } catch (Exception e) {
                            throw new RuntimeException("Error converting optionsData to JSON", e);
                        }

                        variant.setImage(variantResponse.imageData()); // Assuming `image` exists in VariantResponse
                        return variant;
                    })
                    .collect(Collectors.toSet());

            savedProduct.setVariantSet(variants);
        }
        // Save the product and return response
        return convertToProductResponse(savedProduct);
    }
    ProductResponse convertToProductResponse(Product product) {
        Set<VariantResponse> variantResponses = product.getVariantSet()
                .stream()
                .map(variant -> new VariantResponse(variant.getUniqueId(),variant.getOptionsData(),variant.getImage()))
                .collect(Collectors.toSet());
        return new ProductResponse(
                product.getId(),
                product.getUniqueProductId(),
                product.getProductName(),
                product.getProductDesc(),
                product.getPrice(),
                product.getImageData(),
                product.getCategryName(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                variantResponses
        );
    }
    public Map<String, Object> deleteProductOrVariants(Long uniqueProductId, Long[] variantIds) {
        Product product = productRepository.findByUniqueProductId(uniqueProductId).
                orElseThrow(()->new ResourceNotFoundException("Product with Unique Product Id " + uniqueProductId + " is not present." +
                        ""));
        Map<String, Object> result = new HashMap<>();
       // log.info("VariantIds list before going into the condition: " + Arrays.toString(variantIds));

        if (variantIds!=null&&variantIds.length != 0) {
            List<String> messages = new ArrayList<>();
            for (Long id : variantIds) {
                messages.addAll(variantService.deleteVariants(id, uniqueProductId));
            }
            result.put("Result", messages);
        }
        else {
            log.info("Else block called here ");
            productRepository.delete(product);
            result.put("message", "Product with unique product Id "+uniqueProductId+" deleted successfully.");
        }

        return result;
    }


    public Page<ProductResponse> findByCriteria(int page, int size, String sortBy, String order, Long uniqueProductId, Long categoryId, String productName, String categoryName,String optionsData,Long minPrice,Long maxPrice) {
        List<String> errors = new ArrayList<>();

        if (page <0) {
            errors.add("Page number should be positive");
        }
        if (size <= 0) {
            errors.add("Page size should be greater then 0");
        }
        if (!order.matches("(?i)^(asc|desc)$")) {
            errors.add("Invalid order value. It must be 'asc' or 'desc' (case-insensitive).");
        }
        if (uniqueProductId != null && !productRepository.existsByUniqueProductId(uniqueProductId)) {
            errors.add("No Product is present with this product id");
        }
        if (categoryId != null && !productRepository.existsByCategory_Id(categoryId)) {
            errors.add("No Product present with this category Id ");
        }
        if (productName != null && !productRepository.existsByProductName(productName)) {
            errors.add("No product present with this product name");
        }
        if (categoryName != null && !productRepository.existsByCategory_CategoryName(categoryName)) {
            errors.add("No products are present with this category");
        }
        if(optionsData!=null&&!productRepository.existsByVariantOptions(optionsData)){
            errors.add("No products present with this variant data");
        }
        if (minPrice != null && maxPrice != null && minPrice > maxPrice) {
            errors.add("Invalid range: minPrice cannot be greater than maxPrice.");
        }
        else if (minPrice == null && maxPrice != null) {
            minPrice = productRepository.findMinPrice();
        } else if (maxPrice == null && minPrice != null) {
            maxPrice = productRepository.findMaxPrice();
        }



        if (!errors.isEmpty()) {
            throw new CustomValidationException(errors);
        }
        Pageable pageable= PageRequest.of(page,size,Sort.by(Sort.Direction.fromString(order),sortBy));
        ;
        Specification<Product> spec = Specification.where(null);
        if (uniqueProductId != null) {
            spec = spec.and(ProductSpecification.hasUniqueProductId(uniqueProductId));
        }
        if(categoryId!=null){
           spec = spec.and(ProductSpecification.hasCategoryId(categoryId)) ;
        }
        if (StringUtils.hasLength(categoryName)) {
            spec = spec.and(ProductSpecification.hasCategoryName(categoryName));
        }
        if (StringUtils.hasLength(productName)) {
            spec = spec.and(ProductSpecification.hasProductName(productName));
        }
        if (StringUtils.hasLength(optionsData)) {
            spec = spec.and(ProductSpecification.hasVariantWithOptionData(optionsData));
        }
        if (minPrice != null || maxPrice != null) {
            spec = spec.and(ProductSpecification.hasPriceRange(minPrice, maxPrice));
        }
        Page<Product> productPage = productRepository.findAll(spec, pageable);
        return productPage.map(this::convertToProductResponse);
    }
}