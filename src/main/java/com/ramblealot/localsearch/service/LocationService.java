package com.ramblealot.localsearch.service;

import com.ramblealot.localsearch.dto.LocationResponseDTO;
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

        return locations.stream()
                .map(loc -> new LocationResponseDTO(
                        loc.getId(),
                        loc.getName(),
                        loc.getDescription(),
                        loc.getOpeningHours(),
                        loc.getCoordinates().getX(), // Longitude
                        loc.getCoordinates().getY()  // Latitude
                ))
                .collect(Collectors.toList());
    }

    public LocationResponseDTO createLocation(Location location, User adminUser) {
        Location savedLocation = locationRepository.save(location);
        activityLogService.logAction(adminUser, "Created new location: " + savedLocation.getName());

        return new LocationResponseDTO(
                savedLocation.getId(),
                savedLocation.getName(),
                savedLocation.getDescription(),
                savedLocation.getOpeningHours(),
                savedLocation.getCoordinates().getX(),
                savedLocation.getCoordinates().getY()
        );
    }
}
