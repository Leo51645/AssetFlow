package com.github.leo51645.assetflow.security.service;

import com.github.leo51645.assetflow.security.domain.entity.RefreshTokenEntity;
import com.github.leo51645.assetflow.security.repository.RefreshTokenRepository;
import com.github.leo51645.assetflow.user.domain.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    void shouldCreateRefreshTokenAndDeleteOldOnes() {
        // Arrange
        UserEntity user = new UserEntity();
        when(jwtService.generateRefreshToken(user)).thenReturn("refreshToken123");

        // Action
        String refreshToken = refreshTokenService.createRefreshToken(user);

        // Assert
        assertEquals("refreshToken123", refreshToken);
        verify(refreshTokenRepository).deleteAllByUser(user);
        verify(refreshTokenRepository).save(any(RefreshTokenEntity.class));
    }

    @Test
    void shouldRotateRefreshTokenByDeletingOldOnesAndCreatingNewsOnes() {
        // Arrange
        UserEntity user = new UserEntity();
        String oldToken = "oldToken123";
        when(jwtService.generateRefreshToken(user)).thenReturn("refreshToken123");

        // Action
        String refreshToken = refreshTokenService.rotateRefreshToken(oldToken, user);

        // Assert
        assertEquals("refreshToken123", refreshToken);
        verify(refreshTokenRepository).deleteByToken(oldToken);
        verify(refreshTokenRepository).save(any(RefreshTokenEntity.class));
    }

    @Test
    void shouldValidateRefreshTokenAndReturnTokenDueToValidity() {
        // Arrange
        String token =  "refreshToken123";
        UserEntity user = new UserEntity();
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(new RefreshTokenEntity(null, token, Instant.now().plus(5, ChronoUnit.MINUTES), user)));

        // Action
        Optional<RefreshTokenEntity> refreshToken = refreshTokenService.validateRefreshToken(token);

        // Assert
        assertEquals(token, refreshToken.get().getToken());
        verify(refreshTokenRepository, never()).delete(any(RefreshTokenEntity.class)); // checks if method was never called
    }

    @Test
    void shouldNotValidateRefreshTokenDueToTokenNotFound() {
        // Arrange
        String token =  "refreshToken123";
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());

        // Action
        Optional<RefreshTokenEntity> refreshToken = refreshTokenService.validateRefreshToken(token);

        // Assert
        assertEquals(refreshToken, Optional.empty());
    }

    @Test
    void shouldNotValidateRefreshTokenDueToTokenExpired() {
        // Arrange
        String token =  "refreshToken123";
        UserEntity user = new UserEntity();
        when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.of(new RefreshTokenEntity(null, token, Instant.now().minus(5, ChronoUnit.MINUTES), user)));

        // Action
        Optional<RefreshTokenEntity> refreshToken = refreshTokenService.validateRefreshToken(token);

        // Assert
        assertEquals(refreshToken, Optional.empty());
        verify(refreshTokenRepository).delete(any(RefreshTokenEntity.class));
    }

    @Test
    void shouldDeleteAllTokensByUser() {
        // Arrange
        UserEntity user = new UserEntity();

        // Action
        refreshTokenService.deleteAllTokensByUser(user);

        // Assert
        verify(refreshTokenRepository).deleteAllByUser(user);
    }
}