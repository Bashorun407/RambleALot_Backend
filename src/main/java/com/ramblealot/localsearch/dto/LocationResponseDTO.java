package com.ramblealot.localsearch.dto;

import com.ramblealot.localsearch.model.Location;

public record LocationResponseDTO(
        Long id,
        String name,
        String description,
        String openingHours,
        double longitude,
        double latitude
) {
    public static LocationResponseDTO locationToLocationResponseDTO(Location location){
        return new LocationResponseDTO(
                location.getId(),
                location.getName(),
                location.getDescription(),
                location.getOpeningHours(),
                location.getCoordinates().getX(),
                location.getCoordinates().getY()
        );
    }
}
