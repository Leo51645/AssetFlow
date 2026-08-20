package com.github.leo51645.assetflow.security.service;

import com.github.leo51645.assetflow.security.domain.dto.request.AuthRequestDto;
import com.github.leo51645.assetflow.security.domain.dto.response.AuthResponseDto;
import com.github.leo51645.assetflow.security.domain.entity.RefreshTokenEntity;
import com.github.leo51645.assetflow.security.exceptionHandling.exception.InvalidRefreshTokenException;
import com.github.leo51645.assetflow.user.domain.dto.mapper.UserDtoMapper;
import com.github.leo51645.assetflow.user.domain.dto.request.RegisterRequestDto;
import com.github.leo51645.assetflow.user.domain.entity.UserEntity;
import com.github.leo51645.assetflow.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;
    @Mock
    private UserDtoMapper userDtoMapper;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUser() {
        // Arrange
        RegisterRequestDto registerRequestDto = new RegisterRequestDto();
        UserEntity userEntity = new UserEntity();
        AuthResponseDto expectedResponse = AuthResponseDto.builder()
                .accessToken("accessToken123")
                .refreshToken("refreshToken123")
                .build();

        when(userService.createUser(registerRequestDto)).thenReturn(userEntity);
        when(jwtService.generateToken(userEntity)).thenReturn("accessToken123");
        when(refreshTokenService.createRefreshToken(userEntity)).thenReturn("refreshToken123");
        when(userDtoMapper.toAuthResponseDto("accessToken123", "refreshToken123")).thenReturn(expectedResponse);

        // Action
        AuthResponseDto actualResponse = authService.register(registerRequestDto);

        // Assert
        assertEquals(expectedResponse, actualResponse);
        verify(userService).createUser(registerRequestDto);
        verify(jwtService).generateToken(userEntity);
        verify(jwtService).generateToken(userEntity);
        verify(refreshTokenService).createRefreshToken(userEntity);
    }

    @Test
    void shouldAuthenticateUser() {
        // Arrange
        AuthRequestDto request = new AuthRequestDto();
        UserEntity userEntity = new UserEntity();
        AuthResponseDto expectedResponse = AuthResponseDto.builder()
                .accessToken("accessToken123")
                .refreshToken("refreshToken123")
                .build();

        when(userService.getUserByEmail(request.getEmail())).thenReturn(userEntity);
        when(jwtService.generateToken(userEntity)).thenReturn("accessToken123");
        when(refreshTokenService.createRefreshToken(userEntity)).thenReturn("refreshToken123");
        when(userDtoMapper.toAuthResponseDto("accessToken123", "refreshToken123")).thenReturn(expectedResponse);

        // Action
        AuthResponseDto actualResponse = authService.authenticate(request);

        // Assert
        assertEquals(expectedResponse, actualResponse);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService).getUserByEmail(request.getEmail());
        verify(jwtService).generateToken(userEntity);
        verify(refreshTokenService).createRefreshToken(userEntity);
    }

    @Test
    void shouldNotAuthenticateUser() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Invalid credentials"));

        // Action + Assert
        assertThrows(BadCredentialsException.class, () -> authService.authenticate(new AuthRequestDto()));
        verifyNoInteractions(userService, jwtService, refreshTokenService);
    }

    @Test
    void shouldRotateRefreshToken() {
        // Arrange
        String refreshToken = "refreshToken123";
        UserEntity userEntity = new UserEntity();
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .id(1L)
                .token(refreshToken)
                .expiresAt(Instant.now().plus(5, ChronoUnit.MINUTES))
                .user(userEntity)
                .build();
        AuthResponseDto expected = AuthResponseDto.builder()
                .accessToken("newAccessToken123")
                .refreshToken("newRefreshToken123")
                .build();

        when(refreshTokenService.validateRefreshToken(refreshToken)).thenReturn(Optional.of(refreshTokenEntity));
        when(jwtService.isTokenValid(refreshToken, userEntity)).thenReturn(true);
        when(jwtService.generateToken(userEntity)).thenReturn("newAccessToken123");
        when(refreshTokenService.rotateRefreshToken(refreshToken, userEntity)).thenReturn("newRefreshToken123");

        // Action
        AuthResponseDto actual = authService.refreshToken(refreshToken);

        // Assert
        assertThat(actual.getAccessToken()).isEqualTo(expected.getAccessToken());
        assertThat(actual.getRefreshToken()).isEqualTo(expected.getRefreshToken());
        verify(jwtService).isTokenValid(refreshToken, userEntity);
    }

    @Test
    void shouldNotRotateRefreshTokenDueToTokenNotFound() {
        // Arrange
        String refreshToken = "refreshToken123";
        UserEntity userEntity = new UserEntity();

        when(refreshTokenService.validateRefreshToken(refreshToken)).thenThrow(new InvalidRefreshTokenException("RefreshToken not found"));

        // Action + Assert
        assertThrows(InvalidRefreshTokenException.class, () -> authService.refreshToken(refreshToken));
        verify(jwtService, never()).isTokenValid(refreshToken, userEntity);
        verify(jwtService, never()).generateToken(userEntity);
        verify(refreshTokenService, never()).rotateRefreshToken(refreshToken, userEntity);
    }

    @Test
    void shouldNotRotateRefreshTokenDueToTokenExpiredOrTokenInvalid() {
        // Arrange
        String refreshToken = "refreshToken123";
        UserEntity userEntity = new UserEntity();
        RefreshTokenEntity refreshTokenEntity = RefreshTokenEntity.builder()
                .id(1L)
                .token(refreshToken)
                .expiresAt(Instant.now().plus(5, ChronoUnit.MINUTES))
                .user(userEntity)
                .build();

        when(refreshTokenService.validateRefreshToken(refreshToken)).thenReturn(Optional.of(refreshTokenEntity));
        when(jwtService.isTokenValid(refreshToken, userEntity)).thenReturn(false);

        // Action + Assert
        assertThrows(InvalidRefreshTokenException.class, () -> authService.refreshToken(refreshToken));
        verify(refreshTokenService).deleteAllTokensByUser(userEntity);
        verify(jwtService, never()).generateToken(userEntity);
        verify(refreshTokenService, never()).rotateRefreshToken(refreshToken, userEntity);
    }

}