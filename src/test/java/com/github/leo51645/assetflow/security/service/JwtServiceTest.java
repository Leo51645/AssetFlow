package com.github.leo51645.assetflow.security.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    JwtService jwtService = new JwtService();
    private final String TEST_SECRET_KEY = "q6MWk4wkx/FTKLqbLyiIPcFvtMs055OycWnuHoEcbUw=";
    byte[] keyBytes = Decoders.BASE64.decode(TEST_SECRET_KEY);
    private final SecretKey SIGNING_KEY =  Keys.hmacShaKeyFor(keyBytes);


    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", TEST_SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 900000L);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", 604800000L);
        ReflectionTestUtils.setField(jwtService, "signingKey", SIGNING_KEY);
    }

    @Test
    void shouldGenerateTokenWithCorrectSubjectAndClaims() {
        UserDetails user = new User("alfred", "password123", List.of());

        String token = jwtService.generateToken(Map.of("role", "USER"), user);

        assertEquals("alfred", jwtService.extractUsername(token));
        String role = jwtService.extractClaim(token, claims ->  claims.get("role", String.class));
        assertEquals("USER", role);
    }

    @Test
    void shouldReturnTrueDueToValidityOfToken() {
        UserDetails user = new User("john", "password321", List.of());
        String token = jwtService.generateToken(Map.of("role", "ADMIN"), user);

        boolean isValidToken = jwtService.isTokenValid(token, user);

        assertTrue(isValidToken);
    }

    @Test
    void shouldReturnFalseDueToInvalidSignature() {
        UserDetails user = new User("john", "password321", List.of());
        String token = jwtService.generateToken(new User("gustav", "password345", List.of()));

        boolean isValidToken = jwtService.isTokenValid(token, user);

        assertFalse(isValidToken);

    }

    @Test
    void shouldReturnFalseDueToExpiredToken() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 1L);
        UserDetails user = new User("john", "password123", List.of());
        String token = jwtService.generateToken(user);

        Thread.sleep(10);

        assertThrows(ExpiredJwtException.class, () -> jwtService.isTokenValid(token, user));
    }

    @Test
    void shouldThrowIfSecretTooShortAtInit() {
        ReflectionTestUtils.setField(jwtService, "secretKey", Base64.getUrlEncoder().encodeToString("short".getBytes()));
        assertThrows(IllegalStateException.class, () -> jwtService.init());
    }

}