package com.ramblealot.localsearch.dto;

import com.ramblealot.localsearch.model.User;

public record UserResponseDTO(
        Long id,
        String fullName,
        String email,
        String role
) {
    public static UserResponseDTO fromUserToUserResponsDTO(User user){
        return new UserResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().toString()
        );
    }
}
