package com.github.leo51645.assetflow.security.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Slf4j
@Service
public class CookieService {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    private static final int COOKIE_MAX_AGE_SECONDS_ON_LOGOUT = 0;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshTokenExpirationMs;

    @Value("${application.security.jwt.cookie.path}")
    private String cookiePath;

    @Value("${application.security.jwt.cookie.secure}")
    private boolean secureCookie;

    @Value("${application.security.jwt.cookie.same-site}")
    private String sameSite;

    public void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            log.warn("Attempt to add refresh token cookie with empty token");
            return;
        }
        int maxAgeSeconds = (int) (refreshTokenExpirationMs / 1000);
        response.addCookie(buildRefreshTokenCookie(refreshToken, maxAgeSeconds));
        log.debug("Refresh token cookie added successfully");
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        response.addCookie(buildRefreshTokenCookie("", COOKIE_MAX_AGE_SECONDS_ON_LOGOUT));
        log.debug("Refresh token cookie cleared");
    }

    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(cookie -> REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private Cookie buildRefreshTokenCookie(String value, int maxAgeSeconds) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(secureCookie);
        cookie.setPath(cookiePath);
        cookie.setMaxAge(maxAgeSeconds);
        cookie.setAttribute("SameSite", sameSite);
        return cookie;
    }
}
