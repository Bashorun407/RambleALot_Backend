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

        return locations.stream().map(LocationResponseDTO::locationToLocationResponseDTO).toList();
    }

    public LocationResponseDTO createLocation(Location location, User adminUser) {

        Location savedLocation = locationRepository.save(location);
        activityLogService.logAction(adminUser, "Created new location: " + savedLocation.getName());

        return LocationResponseDTO.locationToLocationResponseDTO(savedLocation);
    }
}
