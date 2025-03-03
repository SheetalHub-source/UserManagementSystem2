package com.example.UserManagementSystem.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "option", "value" })
public record VariantAttributes(
        @JsonProperty("option") String option,
        @JsonProperty("value") String value
) {}
