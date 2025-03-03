package com.example.UserManagementSystem.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record VariantRequest(
        Long uniqueId,
        @NotNull(message = "Variant Option Data should not be null")
        @JsonProperty("optionsData") List<VariantAttributes> optionsData) {  // ✅ Corrected spelling
}
/*{ "uniqueProductId": 27040,
        "productName": "Updated Smart Wireless Headphones",
        "productDesc": "Updated Noise-cancelling Bluetooth headphones with superior sound quality and long-lasting battery life.",
        "price": 2999.99,
        "categoryId": 2,
        "variantSet": [
        {    "uniqueId": 26941,
        "optionsData": [
        {
        "option": "Updated Size",
        "value": "Large"
        },
        {
        "option": "Color",
        "value": "Blue"
        }
        ]
        },
        {
        "uniqueId": 26791,
        "optionsData": [
        {
        "option": "Size",
        "value": "Small"
        },
        {
        "option": "Color",
        "value": "White"
        }
        ]
        },
        {
        "uniqueId": 26964,
        "optionsData": [
        {
        "option": "Size",
        "value": "Updated size"
        },
        {
        "option": "Color",
        "value": "Pink color is updated now"
        }
        ]
        }
        ]
        }*/
