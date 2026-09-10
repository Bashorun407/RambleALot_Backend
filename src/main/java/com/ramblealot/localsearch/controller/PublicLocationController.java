package com.ramblealot.localsearch.controller;

import com.ramblealot.localsearch.dto.LocationResponseDTO;
import com.ramblealot.localsearch.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public/locations")
@RequiredArgsConstructor
public class PublicLocationController {

    private final LocationService locationService;
    @GetMapping("/nearby")
    public ResponseEntity<List<LocationResponseDTO>> getNearbyLocations(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam double radiusInMeters) {
        return ResponseEntity.ok(locationService.getLocationsNearParticipant(longitude, latitude, radiusInMeters));
    }
}
