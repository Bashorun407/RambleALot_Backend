package com.ramblealot.localsearch.service;

import com.ramblealot.localsearch.dto.ActivityLogResponseDTO;
import com.ramblealot.localsearch.model.ActivityLog;
import com.ramblealot.localsearch.model.User;
import com.ramblealot.localsearch.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository logRepository;

    public void logAction(User user, String actionDescription) {
        ActivityLog log = ActivityLog.builder()
                .user(user)
                .actionDescription(actionDescription)
                .build();
        logRepository.save(log);
    }

    public List<ActivityLogResponseDTO> getRecentLogs() {

        return logRepository.findAllByOrderByTimestampDesc().stream()
                .map(ActivityLogResponseDTO::logToLogResponseDTO)
                .toList();
    }
}
