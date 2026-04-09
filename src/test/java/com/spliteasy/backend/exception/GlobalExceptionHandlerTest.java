package com.spliteasy.backend.exception;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleDuplicateEmail_ReturnsConflictStatus() {
        DuplicateEmailException exception = new DuplicateEmailException("Email already exists");
        
        ResponseEntity<Map<String, String>> response = handler.handleDuplicateEmail(exception);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Email already exists", response.getBody().get("error"));
    }

    @Test
    void handleInvalidCredentials_ReturnsUnauthorizedStatus() {
        InvalidCredentialsException exception = new InvalidCredentialsException("Invalid credentials");
        
        ResponseEntity<Map<String, String>> response = handler.handleInvalidCredentials(exception);
        
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid credentials", response.getBody().get("error"));
    }

    @Test
    void handleValidationErrors_ReturnsBadRequestStatus() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("registerRequest", "email", "Email is required");
        
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(fieldError));
        
        ResponseEntity<Map<String, String>> response = handler.handleValidationErrors(exception);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email is required", response.getBody().get("email"));
    }
}
