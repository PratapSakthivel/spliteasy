package com.spliteasy.backend.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.spliteasy.backend.dto.AuthResponse;
import com.spliteasy.backend.dto.LoginRequest;
import com.spliteasy.backend.dto.RegisterRequest;
import com.spliteasy.backend.entity.User;
import com.spliteasy.backend.exception.DuplicateEmailException;
import com.spliteasy.backend.exception.InvalidCredentialsException;
import com.spliteasy.backend.repository.UserRepository;
import com.spliteasy.backend.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        // Check email uniqueness
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email already in use");
        }

        // Hash password
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Save user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(hashedPassword);
        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        // Find user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token);
    }
}
