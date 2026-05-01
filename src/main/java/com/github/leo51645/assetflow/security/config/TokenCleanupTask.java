package com.github.leo51645.assetflow.security.config;

import com.github.leo51645.assetflow.security.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanupTask {

    private final RefreshTokenRepository refreshTokenRepository;

    // every day at 3 am and cleans expiredTokens
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanUpExpiredTokens() {
        int deletedCount = refreshTokenRepository.deleteExpiredTokens(Instant.now());
        log.info("Cleaned up {} expired refresh tokens", deletedCount);
    }
}
