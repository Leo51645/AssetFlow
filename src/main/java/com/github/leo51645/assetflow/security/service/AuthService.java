package com.github.leo51645.assetflow.security.service;

import com.github.leo51645.assetflow.security.domain.dto.request.AuthRequestDto;
import com.github.leo51645.assetflow.security.domain.dto.response.AuthResponseDto;
import com.github.leo51645.assetflow.security.domain.entity.RefreshTokenEntity;
import com.github.leo51645.assetflow.security.exception.InvalidRefreshTokenException;
import com.github.leo51645.assetflow.user.domain.dto.mapper.UserDtoMapper;
import com.github.leo51645.assetflow.user.domain.dto.request.RegisterRequestDto;
import com.github.leo51645.assetflow.user.domain.entity.UserEntity;
import com.github.leo51645.assetflow.user.repository.UserRepository;
import com.github.leo51645.assetflow.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {

        UserEntity userEntity = userService.createUser(request);

        String accessToken = jwtService.generateToken(userEntity);
        String refreshToken = refreshTokenService.createRefreshToken(userEntity);

        return userDtoMapper.toAuthResponseDto(userEntity,accessToken, refreshToken);
    }

    @Transactional
    public AuthResponseDto authenticate(AuthRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + request.getEmail()));

        log.info("User authenticated: {} with role: {}", request.getEmail(), user.getRole());

        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return userDtoMapper.toAuthResponseDto(user, accessToken, refreshToken);
    }

    @Transactional
    public AuthResponseDto refreshToken(String refreshToken) {
        RefreshTokenEntity storedToken = refreshTokenService.validateRefreshToken(refreshToken)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid or expired refresh token"));

        UserEntity user = storedToken.getUser();

        if (!jwtService.isTokenValid(refreshToken, user)) {
            refreshTokenService.deleteAllTokensByUser(user);
            throw new InvalidRefreshTokenException("Invalid refresh token - all sessions revoked");
        }

        log.info("Token refreshed for user: {}", user.getEmail());

        String newAccessToken = jwtService.generateToken(user);
        String newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken, user);

        return AuthResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }

        refreshTokenService.validateRefreshToken(refreshToken)
                .ifPresentOrElse(
                        storedToken -> refreshTokenService.deleteAllTokensByUser(storedToken.getUser()),
                        () -> log.debug("Logout skipped token cleanup because refresh token is invalid or expired")
                );
    }
}
