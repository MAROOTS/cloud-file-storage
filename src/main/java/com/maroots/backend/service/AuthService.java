package com.maroots.backend.service;

import com.maroots.backend.dto.AuthResponse;
import com.maroots.backend.dto.LoginRequest;
import com.maroots.backend.dto.RegisterRequest;
import com.maroots.backend.model.AppUser;
import com.maroots.backend.repository.UserRepository;
import com.maroots.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        var user = AppUser.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role("USER")
                .build();
        userRepository.save(user);
        return AuthResponse.builder()
                .token(jwtService.generateToken(user.getEmail()))
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return AuthResponse.builder()
                .token(jwtService.generateToken(user.getEmail()))
                .build();
    }

}
