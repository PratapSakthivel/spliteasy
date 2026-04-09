package com.spliteasy.backend.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private static final String TEST_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final Long TEST_EXPIRATION = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "expiration", TEST_EXPIRATION);
    }

    @Test
    void generateToken_shouldReturnNonNullToken() {
        String token = jwtUtil.generateToken("test@example.com");
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractEmail_shouldReturnCorrectEmail() {
        String email = "test@example.com";
        String token = jwtUtil.generateToken(email);
        
        String extractedEmail = jwtUtil.extractEmail(token);
        
        assertEquals(email, extractedEmail);
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        String token = jwtUtil.generateToken("test@example.com");
        
        boolean isValid = jwtUtil.validateToken(token);
        
        assertTrue(isValid);
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidToken() {
        String invalidToken = "invalid.token.here";
        
        boolean isValid = jwtUtil.validateToken(invalidToken);
        
        assertFalse(isValid);
    }

    @Test
    void validateToken_shouldReturnFalseForTamperedToken() {
        String token = jwtUtil.generateToken("test@example.com");
        String tamperedToken = token.substring(0, token.length() - 5) + "XXXXX";
        
        boolean isValid = jwtUtil.validateToken(tamperedToken);
        
        assertFalse(isValid);
    }

    @Test
    void isTokenExpired_shouldReturnFalseForFreshToken() {
        String token = jwtUtil.generateToken("test@example.com");
        
        boolean isExpired = jwtUtil.isTokenExpired(token);
        
        assertFalse(isExpired);
    }

    @Test
    void isTokenExpired_shouldReturnTrueForExpiredToken() {
        // Create a JwtUtil with very short expiration
        JwtUtil shortExpirationJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(shortExpirationJwtUtil, "secret", TEST_SECRET);
        ReflectionTestUtils.setField(shortExpirationJwtUtil, "expiration", -1000L); // Already expired
        
        String token = shortExpirationJwtUtil.generateToken("test@example.com");
        
        boolean isExpired = shortExpirationJwtUtil.isTokenExpired(token);
        
        assertTrue(isExpired);
    }

    @Test
    void generateToken_shouldGenerateDifferentTokensForDifferentEmails() {
        String token1 = jwtUtil.generateToken("user1@example.com");
        String token2 = jwtUtil.generateToken("user2@example.com");
        
        assertNotEquals(token1, token2);
    }
}
