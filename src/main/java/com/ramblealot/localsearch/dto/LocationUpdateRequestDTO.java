package com.ramblealot.localsearch.dto;

public record LocationUpdateRequestDTO(
        String name,
        String description,
        String openingHours,
        Double longitude,
        Double latitude,
        Boolean isActive
) {
}
