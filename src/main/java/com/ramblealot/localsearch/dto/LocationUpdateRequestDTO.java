package com.ramblealot.localsearch.dto;

public record LocationUpdateRequestDTO(
        String name,
        String imageUrl,
        String description,
        String openingHours,
        Double longitude,
        Double latitude,
        Boolean isActive
) {
}
