package com.ramblealot.localsearch.controller;

import com.ramblealot.localsearch.dto.ActivityLogResponseDTO;
import com.ramblealot.localsearch.dto.LocationRequestDTO;
import com.ramblealot.localsearch.dto.LocationResponseDTO;
import com.ramblealot.localsearch.model.Location;
import com.ramblealot.localsearch.model.User;
import com.ramblealot.localsearch.service.ActivityLogService;
import com.ramblealot.localsearch.service.LocationService;
import com.ramblealot.localsearch.service.UserService;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ActivityLogService activityLogService;
    private final LocationService locationService;
    private final UserService userService;

    @GetMapping("/logs")
    public ResponseEntity<List<ActivityLogResponseDTO>> getRecentLogs() {
        return ResponseEntity.ok(activityLogService.getRecentLogs());
    }

    @PostMapping("/locations")
    public ResponseEntity<LocationResponseDTO> createLocation(@RequestBody LocationRequestDTO request, Authentication authentication) {
        User adminUser = userService.findEntityByEmail(authentication.getName());

        // Construct the PostGIS Point using JTS
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point point = geometryFactory.createPoint(new Coordinate(request.longitude(), request.latitude()));

        Location location = Location.builder()
                .name(request.name())
                .description(request.description())
                .openingHours(request.openingHours())
                .coordinates(point)
                .isActive(true)
                .build();

        return ResponseEntity.ok(locationService.createLocation(location, adminUser));
    }
}
