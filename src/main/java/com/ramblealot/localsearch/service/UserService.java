package com.ramblealot.localsearch.service;

import com.ramblealot.localsearch.dto.RegisterRequestDTO;
import com.ramblealot.localsearch.dto.UserOnboardingRequestDTO;
import com.ramblealot.localsearch.dto.UserResponseDTO;
import com.ramblealot.localsearch.model.Role;
import com.ramblealot.localsearch.model.User;
import com.ramblealot.localsearch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ActivityLogService activityLogService;

    public UserResponseDTO registerOrganiser(RegisterRequestDTO registerRequestDTO) {

        User user = User.builder()
                .email(registerRequestDTO.email())
                .password(registerRequestDTO.password())
                .fullName(registerRequestDTO.fullName())
                .organizationName(registerRequestDTO.organizationName())
                .role(registerRequestDTO.role())
                .isActive(true)
                .build();

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        activityLogService.logAction(user, "Registered Organiser: " + savedUser.getEmail());

        return UserResponseDTO.userToUserResponseDTO(savedUser);
    }

    public User findEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User profile not found."));
    }

    public UserResponseDTO onboardUser(UserOnboardingRequestDTO userOnboardingRequestDTO, User invitingAdmin) {
        // Check if email already exists
        if (userRepository.findByEmail(userOnboardingRequestDTO.email()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists.");
        }

        User newUser = User.builder()
                .email(userOnboardingRequestDTO.email())
                .fullName(userOnboardingRequestDTO.fullName())
                .password(passwordEncoder.encode(userOnboardingRequestDTO.temporaryPassword()))
                .role(userOnboardingRequestDTO.role())
                .organizationName(userOnboardingRequestDTO.organizationName())
                .isActive(true)
                .build();

        User savedUser = userRepository.save(newUser);
        activityLogService.logAction(invitingAdmin, "Onboarded a new user: " + newUser.getEmail());

        return UserResponseDTO.userToUserResponseDTO(savedUser);
    }

    public List<UserResponseDTO> getUsersByOrganization(String organizationName) {
        return userRepository.findAllByOrganizationNameAndIsActiveTrue(organizationName).stream()
                .map(UserResponseDTO::userToUserResponseDTO)
                .toList();
    }

    public String resetPasswordByAdmin(Long userId, String newPassword, User executingAdmin) {
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Ensure the admin only resets passwords for their own organization
        if (!targetUser.getOrganizationName().equals(executingAdmin.getOrganizationName())) {
            throw new SecurityException("Unauthorized to modify users outside your organization.");
        }

        targetUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(targetUser);
        return "Password updated successfully";
    }

    public void deactivateUser(Long userId, User executingAdmin) {
        if (executingAdmin.getId().equals(userId)) {
            throw new IllegalArgumentException("Admins cannot deactivate their own accounts.");
        }

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        if (!targetUser.getOrganizationName().equals(executingAdmin.getOrganizationName())) {
            throw new SecurityException("Unauthorized to modify users outside your organization.");
        }

        targetUser.setActive(false);
        userRepository.save(targetUser);
    }
}
