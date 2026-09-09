package com.ramblealot.localsearch.dto;

public record LocationResponseDTO(
        Long id,
        String name,
        String description,
        String openingHours,
        double longitude,
        double latitude
) {
}
