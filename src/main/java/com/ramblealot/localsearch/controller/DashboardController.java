package com.ramblealot.localsearch.controller;

import com.ramblealot.localsearch.dto.*;
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

    @PostMapping("/users/onboard")
    public ResponseEntity<UserResponseDTO> onboardUser(
            @RequestBody UserOnboardingRequestDTO request,
            Authentication authentication) {

        User currentAdmin = userService.findEntityByEmail(authentication.getName());
        UserResponseDTO response = userService.onboardUser(request, currentAdmin);
        activityLogService.logAction(currentAdmin, "Onboarded user: " + response.email()
                + " as " + response.role());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponseDTO>> getOrganizationUsers(Authentication authentication) {
        User currentAdmin = userService.findEntityByEmail(authentication.getName());
        return ResponseEntity.ok(userService.getUsersByOrganization(currentAdmin.getOrganizationName()));
    }

    @PatchMapping("/users/{id}/reset-password")
    public ResponseEntity<String> resetUserPassword(
            @PathVariable Long id,
            @RequestParam String newPassword,
            Authentication authentication) {
        User currentAdmin = userService.findEntityByEmail(authentication.getName());
        String response = userService.resetPasswordByAdmin(id, newPassword, currentAdmin);
        activityLogService.logAction(currentAdmin, "Reset password for user ID: " + id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/locations/{id}")
    public ResponseEntity<LocationResponseDTO> updateLocation(
            @PathVariable Long id,
            @RequestBody LocationUpdateRequestDTO request,
            Authentication authentication) {
        User currentAdmin = userService.findEntityByEmail(authentication.getName());
        return ResponseEntity.ok(locationService.updateLocation(id, request, currentAdmin));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> removeUser(@PathVariable Long id, Authentication authentication) {
        User currentAdmin = userService.findEntityByEmail(authentication.getName());
        userService.deactivateUser(id, currentAdmin);
        activityLogService.logAction(currentAdmin, "Deactivated user account ID: " + id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/locations/{id}")
    public ResponseEntity<Void> removeLocation(@PathVariable Long id, Authentication authentication) {
        User currentAdmin = userService.findEntityByEmail(authentication.getName());
        locationService.deactivateLocation(id, currentAdmin);
        return ResponseEntity.noContent().build();
    }
}
