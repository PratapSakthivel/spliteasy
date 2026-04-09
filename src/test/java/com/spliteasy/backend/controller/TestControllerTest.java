package com.spliteasy.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@SpringBootTest
class TestControllerTest {

    @Autowired
    private TestController testController;

    @Test
    void testControllerBeanExists() {
        assertNotNull(testController, "TestController bean should be created");
    }

    @Test
    void testEndpoint_returnsSuccessMessage() {
        ResponseEntity<String> response = testController.test();
        
        assertEquals(200, response.getStatusCode().value(), "Should return 200 OK");
        assertEquals("Test endpoint accessed successfully", response.getBody(), 
            "Should return success message");
    }
}
