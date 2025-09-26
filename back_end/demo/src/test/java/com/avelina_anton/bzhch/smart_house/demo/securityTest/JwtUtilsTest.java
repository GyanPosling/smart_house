package com.avelina_anton.bzhch.smart_house.demo.securityTest;

import com.avelina_anton.bzhch.smart_house.demo.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwtUtilsTest {

    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        // Используем реальные значения, так как это unit-тест
        jwtUtils = new JwtUtils("secretsecretsecretsecretsecretsecretsecretsecret", 3600000); // 1 час
    }

    @Test
    void generateJwtToken_ShouldGenerateValidToken() {
        String token = jwtUtils.generateJwtToken("testuser");

        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void getUsernameFromJwt_ValidToken_ShouldReturnUsername() {
        String token = jwtUtils.generateJwtToken("testuser");

        String username = jwtUtils.getUsernameFromJwt(token);

        assertEquals("testuser", username);
    }

    @Test
    void validateJwtToken_ValidToken_ShouldReturnTrue() {
        String token = jwtUtils.generateJwtToken("testuser");

        boolean isValid = jwtUtils.validateJwtToken(token);

        assertTrue(isValid);
    }

    @Test
    void validateJwtToken_InvalidToken_ShouldReturnFalse() {
        boolean isValid = jwtUtils.validateJwtToken("invalid.token.here");

        assertFalse(isValid);
    }

    @Test
    void validateToken_WithUserDetails_ShouldReturnTrue() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");

        String token = jwtUtils.generateJwtToken("testuser");

        boolean isValid = jwtUtils.validateToken(token, userDetails);

        assertTrue(isValid);
    }
}