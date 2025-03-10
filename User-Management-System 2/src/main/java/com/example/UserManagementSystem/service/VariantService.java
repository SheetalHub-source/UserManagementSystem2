package com.example.UserManagementSystem.service;

import com.example.UserManagementSystem.ExceptionHandling.ResourceNotFoundException;
import com.example.UserManagementSystem.dto.VariantAttributes;
import com.example.UserManagementSystem.dto.VariantRequest;
import com.example.UserManagementSystem.dto.VariantResponse;
import com.example.UserManagementSystem.Model.Product;
import com.example.UserManagementSystem.Model.Variant;
import com.example.UserManagementSystem.repository.VariantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VariantService {
    @Autowired
    private VariantRepository variantRepository;

    /*private String encodeImageToBase64(MultipartFile image) throws IOException {
        byte[] imageBytes = image.getBytes();
        return Base64.getEncoder().encodeToString(imageBytes);  // Correct usage
    }*/
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String convertToJson(List<VariantAttributes> attributes) throws Exception {
        // Ensure order is maintained before saving
        List<Map<String, String>> orderedList = attributes.stream()
                .map(attr -> {
                    Map<String, String> orderedMap = new LinkedHashMap<>();
                    orderedMap.put("option", attr.option());  // "option" first
                    orderedMap.put("value", attr.value());  // "value" second
                    return orderedMap;
                })
                .toList();

        return objectMapper.writeValueAsString(orderedList);
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

    public static VariantResponse convertToResponseDTO(Variant variant) {
        VariantResponse responseDTO = new VariantResponse(variant.getUniqueId(), variant.getOptionsData(), variant.getImage());
        return responseDTO;
    }

    /// CREATE AND UPDATE VARIANT
    public VariantResponse createAndUpdateVariant(VariantRequest variantRequest, MultipartFile variantImage, Product product) throws Exception {
        String imageUrl = saveImage(variantImage);
        if (variantRequest.uniqueId() == null) {
            Variant variant = new Variant();

            String optionsDataJson = convertToJson(variantRequest.optionsData());
            variant.setOptionsData(optionsDataJson);
            variant.setImage(imageUrl);
            variant.setProduct(product);
            System.out.println("Before Saved variant "+variant);
            System.out.println("Before Optionsdata of saved variant "+ variant.getOptionsData());
            Variant savedVariant = variantRepository.save(variant);
            System.out.println("Saved variant "+savedVariant);
            System.out.println("Optionsdata of saved variant "+ savedVariant.getOptionsData());
            return convertToResponseDTO(variant);
        } else {
            Variant existingVariant = variantRepository.findByUniqueId(variantRequest.uniqueId());
            if (existingVariant == null)
                throw new ResourceNotFoundException("Variant with this Unique id: " + variantRequest.uniqueId() + " is not found");
            ObjectMapper objectMapper = new ObjectMapper();
            String optionsDataJson = objectMapper.writeValueAsString(variantRequest.optionsData());
            existingVariant.setOptionsData(optionsDataJson);
            existingVariant.setImage(imageUrl);
            existingVariant.setProduct(product);
            Variant updateVariant = variantRepository.save(existingVariant);
            return convertToResponseDTO(updateVariant);
        }

    }

    public List<String> deleteVariants( Long variantId, Long uniqueProductId) {
           List<String> msg = new ArrayList<>();
            Variant existingVariant = variantRepository.findByUniqueId(variantId);

            if (existingVariant==null) {
                msg.add("Variant with Unique ID " + variantId + " not found for deletion.");
            }
            else if (existingVariant!=null&&existingVariant.getProduct().getUniqueProductId().equals(uniqueProductId)) {
                msg.add("Variant with Unique ID " + variantId + " deleted successfully.");
                variantRepository.delete(existingVariant);
            } else {
                msg.add("Variant ID " + variantId + " cannot be deleted as it is not linked to Product ID " + uniqueProductId + ".");
            }
        return msg;
    }



    public List<VariantResponse> findByProductId(Long uniqueProductId) {

          List<Variant> variants=variantRepository.findByProductId(uniqueProductId);
            return variants.stream()
                .map(variant -> new VariantResponse(
                        variant.getUniqueId(),
                        variant.getOptionsData(),
                        variant.getImage()
                ))
                .collect(Collectors.toList());
    }

    public void deleteAll(List<Variant> productVariants) {
        variantRepository.deleteAll(productVariants);
    }

}
