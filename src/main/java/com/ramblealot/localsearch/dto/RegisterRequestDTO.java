package com.ramblealot.localsearch.dto;

import com.ramblealot.localsearch.model.Role;

public record RegisterRequestDTO(
        String email,
        String password,
        String fullName,
        Role role
) {
}
