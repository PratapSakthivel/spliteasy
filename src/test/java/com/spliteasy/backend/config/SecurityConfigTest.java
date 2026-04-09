package com.spliteasy.backend.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Test
    void testBCryptPasswordEncoderBeanExists() {
        assertNotNull(passwordEncoder, "BCryptPasswordEncoder bean should be created");
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder, 
            "Password encoder should be BCryptPasswordEncoder");
    }

    @Test
    void testSecurityFilterChainBeanExists() {
        assertNotNull(securityFilterChain, "SecurityFilterChain bean should be created");
    }

    @Test
    void testPasswordEncoderHashesPasswords() {
        String rawPassword = "testPassword123";
        String hashedPassword = passwordEncoder.encode(rawPassword);
        
        assertNotNull(hashedPassword, "Hashed password should not be null");
        assertNotEquals(rawPassword, hashedPassword, "Hashed password should not equal raw password");
        assertTrue(passwordEncoder.matches(rawPassword, hashedPassword), 
            "Password encoder should validate correct password");
        assertFalse(passwordEncoder.matches("wrongPassword", hashedPassword), 
            "Password encoder should reject incorrect password");
    }
}
