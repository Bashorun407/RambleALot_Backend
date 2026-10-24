package com.ramblealot.localsearch.dto;

public record LocationRequestDTO(
        String name,
        String imageUrl,
        String description,
        String openingHours,
        double longitude,
        double latitude) {
}
