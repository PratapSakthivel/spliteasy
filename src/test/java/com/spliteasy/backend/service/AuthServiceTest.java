package com.spliteasy.backend.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.spliteasy.backend.dto.AuthResponse;
import com.spliteasy.backend.dto.LoginRequest;
import com.spliteasy.backend.dto.RegisterRequest;
import com.spliteasy.backend.entity.User;
import com.spliteasy.backend.exception.DuplicateEmailException;
import com.spliteasy.backend.exception.InvalidCredentialsException;
import com.spliteasy.backend.repository.UserRepository;
import com.spliteasy.backend.security.JwtUtil;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_WithValidCredentials_CreatesUserAndReturnsToken() {
        // Arrange
        RegisterRequest request = new RegisterRequest("test@example.com", "password123");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");
        when(jwtUtil.generateToken(request.getEmail())).thenReturn("jwt-token");

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        verify(userRepository).findByEmail(request.getEmail());
        verify(passwordEncoder).encode(request.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtUtil).generateToken(request.getEmail());
    }

    @Test
    void register_WithDuplicateEmail_ThrowsDuplicateEmailException() {
        // Arrange
        RegisterRequest request = new RegisterRequest("existing@example.com", "password123");
        User existingUser = new User(1L, "existing@example.com", "hashedPassword");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        assertThrows(DuplicateEmailException.class, () -> authService.register(request));
        verify(userRepository).findByEmail(request.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_WithValidCredentials_ReturnsToken() {
        // Arrange
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        User user = new User(1L, "test@example.com", "hashedPassword");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(user.getEmail())).thenReturn("jwt-token");

        // Act
        AuthResponse response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        verify(userRepository).findByEmail(request.getEmail());
        verify(passwordEncoder).matches(request.getPassword(), user.getPassword());
        verify(jwtUtil).generateToken(user.getEmail());
    }

    @Test
    void login_WithNonExistentEmail_ThrowsInvalidCredentialsException() {
        // Arrange
        LoginRequest request = new LoginRequest("nonexistent@example.com", "password123");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        verify(userRepository).findByEmail(request.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void login_WithIncorrectPassword_ThrowsInvalidCredentialsException() {
        // Arrange
        LoginRequest request = new LoginRequest("test@example.com", "wrongPassword");
        User user = new User(1L, "test@example.com", "hashedPassword");
        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        verify(userRepository).findByEmail(request.getEmail());
        verify(passwordEncoder).matches(request.getPassword(), user.getPassword());
        verify(jwtUtil, never()).generateToken(anyString());
    }
}
