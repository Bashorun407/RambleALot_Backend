package com.ramblealot.localsearch.service;

import com.ramblealot.localsearch.dto.UserOnboardingRequestDTO;
import com.ramblealot.localsearch.dto.UserResponseDTO;
import com.ramblealot.localsearch.model.User;
import com.ramblealot.localsearch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO registerOrganiser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

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
                .isActive(true)
                .build();

        User savedUser = userRepository.save(newUser);

        return UserResponseDTO.userToUserResponseDTO(savedUser);
    }
}
