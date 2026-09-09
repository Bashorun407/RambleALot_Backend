package com.ramblealot.localsearch.dto;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        String role
) {
}
