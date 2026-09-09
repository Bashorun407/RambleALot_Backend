package com.ramblealot.localsearch.dto;

import java.time.LocalDateTime;

public record ActivityLogResponseDTO(
        Long id,
        String actionDescription,
        String userEmail,
        LocalDateTime timestamp
) {
}
