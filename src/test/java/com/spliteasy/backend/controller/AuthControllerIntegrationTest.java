package com.spliteasy.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spliteasy.backend.dto.LoginRequest;
import com.spliteasy.backend.dto.RegisterRequest;
import com.spliteasy.backend.repository.UserRepository;

/**
 * Integration tests for authentication flow
 * Validates: Requirements 1.1, 2.1, 4.1, 4.2, 4.3
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AuthControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Set up MockMvc with the web application context and Spring Security
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        
        // Clean database before each test
        userRepository.deleteAll();
    }

    /**
     * Test complete registration flow
     * Validates: Requirement 1.1 - User registration with valid credentials
     */
    @Test
    void testCompleteRegistrationFlow() throws Exception {
        RegisterRequest request = new RegisterRequest("test@example.com", "password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    /**
     * Test complete login flow
     * Validates: Requirement 2.1 - User login with valid credentials
     */
    @Test
    void testCompleteLoginFlow() throws Exception {
        // First register a user
        RegisterRequest registerRequest = new RegisterRequest("login@example.com", "password123");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Then login with the same credentials
        LoginRequest loginRequest = new LoginRequest("login@example.com", "password123");
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    /**
     * Test accessing protected endpoint with valid token
     * Validates: Requirement 4.2 - Valid token allows access to protected resources
     */
    @Test
    void testAccessProtectedEndpointWithValidToken() throws Exception {
        // Register and get token
        RegisterRequest registerRequest = new RegisterRequest("protected@example.com", "password123");
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(responseBody).get("token").asText();

        // Access protected endpoint with valid token
        mockMvc.perform(get("/api/test")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Test endpoint accessed successfully"));
    }

    /**
     * Test accessing protected endpoint without token
     * Validates: Requirement 4.1, 4.3 - Requests without token are rejected
     */
    @Test
    void testAccessProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/api/test"))
                .andExpect(status().isForbidden());
    }

    /**
     * Test accessing protected endpoint with invalid token
     * Validates: Requirement 4.3 - Invalid tokens are rejected
     */
    @Test
    void testAccessProtectedEndpointWithInvalidToken() throws Exception {
        String invalidToken = "invalid.jwt.token";

        mockMvc.perform(get("/api/test")
                .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isForbidden());
    }
}
