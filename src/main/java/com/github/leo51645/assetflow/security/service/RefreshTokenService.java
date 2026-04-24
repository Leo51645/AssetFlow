package com.github.leo51645.assetflow.security.service;

import com.github.leo51645.assetflow.security.domain.entity.RefreshTokenEntity;
import com.github.leo51645.assetflow.security.repository.RefreshTokenRepository;
import com.github.leo51645.assetflow.user.domain.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpirationMs;

    // creates new RefreshToken and all the other older ones are getting deleted
    @Transactional
    public String createRefreshToken(UserEntity user) {
        refreshTokenRepository.deleteAllByUser(user);

        String token = jwtService.generateRefreshToken(user);

        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .token(token)
                .user(user)
                .expiresAt(Instant.now().plusMillis(refreshTokenExpirationMs))
                .build();

        refreshTokenRepository.save(refreshToken);
        log.debug("Refresh token created for user: {}", user.getEmail());

        return token;
    }

    // rotates the refreshToken by deleting the old one and creating a new one
    @Transactional
    public String rotateRefreshToken(String oldToken, UserEntity user) {
        refreshTokenRepository.deleteByToken(oldToken);

        String newToken = jwtService.generateRefreshToken(user);

        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .token(newToken)
                .user(user)
                .expiresAt(Instant.now().plusMillis(refreshTokenExpirationMs))
                .build();

        refreshTokenRepository.save(refreshToken);
        log.debug("Refresh token rotated for user: {}", user.getEmail());

        return newToken;
    }

    // validates a token by existence in DB and non expiry -> these one are getting deleted instantly
    @Transactional
    public Optional<RefreshTokenEntity> validateRefreshToken(String token) {
        Optional<RefreshTokenEntity> refreshToken = refreshTokenRepository.findByToken(token);

        if (refreshToken.isEmpty()) {
            log.warn("Refresh token not found in database");
            return Optional.empty();
        }

        RefreshTokenEntity rt = refreshToken.get();

        if (rt.isExpired()) {
            log.warn("Refresh token expired for user: {}", rt.getUser().getEmail());
            refreshTokenRepository.delete(rt);
            return Optional.empty();
        }

        return refreshToken;
    }

    // deletes all RefreshTokens from a user when he logs himself out for example
    @Transactional
    public void deleteAllTokensByUser(UserEntity user) {
        refreshTokenRepository.deleteAllByUser(user);
        log.info("All refresh tokens deleted for user: {}", user.getEmail());
    }
}
