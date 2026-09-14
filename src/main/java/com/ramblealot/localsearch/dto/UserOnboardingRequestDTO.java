package com.ramblealot.localsearch.dto;

import com.ramblealot.localsearch.model.Role;

public record UserOnboardingRequestDTO(
        String email,
        String fullName,
        String temporaryPassword,
        Role role
) {
}
