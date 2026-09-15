package com.ramblealot.localsearch.dto;

import com.ramblealot.localsearch.model.ActivityLog;

import java.time.LocalDateTime;

public record ActivityLogResponseDTO(
        Long id,
        String actionDescription,
        String userEmail,
        LocalDateTime timestamp
) {
    public static ActivityLogResponseDTO logToLogResponseDTO(ActivityLog log){
        return new ActivityLogResponseDTO(
                log.getId(),
                log.getActionDescription(),
                log.getUser().getEmail(),
                log.getTimestamp()
        );
    }
}
