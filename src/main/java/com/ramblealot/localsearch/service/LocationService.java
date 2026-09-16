package com.ramblealot.localsearch.service;

import com.ramblealot.localsearch.dto.LocationResponseDTO;
import com.ramblealot.localsearch.dto.LocationUpdateRequestDTO;
import com.ramblealot.localsearch.model.Location;
import com.ramblealot.localsearch.model.User;
import com.ramblealot.localsearch.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;
    private final ActivityLogService activityLogService;

    @Cacheable(value = "locations", key = "#latitude + '-' + #longitude + '-' + #radiusInMeters")
    public List<LocationResponseDTO> getLocationsNearParticipant(double longitude, double latitude, double radiusInMeters) {
        List<Location> locations = locationRepository.findLocationsWithinRadius(longitude, latitude, radiusInMeters);

        return locations.stream().map(LocationResponseDTO::locationToLocationResponseDTO).toList();
    }

    public LocationResponseDTO createLocation(Location location, User adminUser) {

        Location savedLocation = locationRepository.save(location);
        activityLogService.logAction(adminUser, "Created new location: " + savedLocation.getName());

        return LocationResponseDTO.locationToLocationResponseDTO(savedLocation);
    }

    public LocationResponseDTO updateLocation(Long locationId, LocationUpdateRequestDTO dto, User adminUser) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location not found with id: " + locationId));

        if (dto.name() != null) location.setName(dto.name());
        if (dto.description() != null) location.setDescription(dto.description());
        if (dto.openingHours() != null) location.setOpeningHours(dto.openingHours());
        if (dto.isActive() != null) location.setActive(dto.isActive());

        if (dto.longitude() != null && dto.latitude() != null) {
            org.locationtech.jts.geom.GeometryFactory geometryFactory =
                    new org.locationtech.jts.geom.GeometryFactory(new org.locationtech.jts.geom.PrecisionModel(), 4326);
            location.setCoordinates(geometryFactory.createPoint(
                    new org.locationtech.jts.geom.Coordinate(dto.longitude(), dto.latitude())
            ));
        }

        Location updated = locationRepository.save(location);
        activityLogService.logAction(adminUser, "Updated location details for: " + updated.getName());

        return LocationResponseDTO.locationToLocationResponseDTO(updated);
    }

    public void deactivateLocation(Long locationId, User adminUser) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new IllegalArgumentException("Location not found with id: " + locationId));

        location.setActive(false);
        locationRepository.save(location);
        activityLogService.logAction(adminUser, "Deactivated location: " + location.getName());
    }
}
