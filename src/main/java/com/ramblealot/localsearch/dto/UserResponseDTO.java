package com.ramblealot.localsearch.dto;

public record UserResponseDTO(
        Long id,
        String fullName,
        String email,
        String role
) {
}
