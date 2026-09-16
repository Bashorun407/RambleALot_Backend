package com.ramblealot.localsearch.controller;

import com.ramblealot.localsearch.dto.AuthResponseDTO;
import com.ramblealot.localsearch.dto.LoginRequestDTO;
import com.ramblealot.localsearch.dto.RegisterRequestDTO;
import com.ramblealot.localsearch.dto.UserResponseDTO;
import com.ramblealot.localsearch.model.User;
import com.ramblealot.localsearch.security.CustomUserDetailsService;
import com.ramblealot.localsearch.security.JwtService;
import com.ramblealot.localsearch.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody RegisterRequestDTO request) {

        return ResponseEntity.ok(userService.registerOrganiser(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        String token = jwtService.generateToken(userDetails);
        return ResponseEntity.ok(new AuthResponseDTO(token));
    }
}
