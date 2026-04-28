package com.github.leo51645.assetflow.security.handler;

import com.github.leo51645.assetflow.security.service.JwtService;
import com.github.leo51645.assetflow.security.service.RefreshTokenService;
import com.github.leo51645.assetflow.user.repository.UserRepository;
import com.github.leo51645.assetflow.user.util.UserUtility;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    @Value("${application.security.jwt.cookie-name}")
    private String cookieName;

    @Value("${application.security.jwt.cookie.same-site:Strict}")
    private String sameSite;

    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserUtility userUtility;

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        String username = resolveUsername(request, authentication);

        if (username != null) {
            userRepository.findByEmail(username).ifPresentOrElse(
                    user -> {
                        refreshTokenService.deleteAllTokensByUser(user);
                        log.info("Logout successful for userId: {}", user.getId());
                        sendResponse(response, HttpServletResponse.SC_OK, "Logout successful");
                    },
                    () -> {
                        log.warn("Logout: user not found for username: {}", userUtility.maskEmail(username));
                        sendResponse(response, HttpServletResponse.SC_NOT_FOUND, "User not found");
                    }
            );
        } else {
            log.warn("Logout called but no user could be resolved from request");
            sendResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "No user could be resolved");
        }

        clearCookie(request, response);
    }

    private String resolveUsername(HttpServletRequest request, Authentication authentication) {
        if (authentication != null && authentication.getName() != null) {
            return authentication.getName();
        }

        String token = extractToken(request);
        if (token != null) {
            try {
                return jwtService.extractUsername(token);
            } catch (JwtException e) {
                log.warn("Logout: invalid token: {}", e.getMessage());
            }
        }

        return null;
    }

    private String extractToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookieName.equals(cookie.getName())) {
                    String value = cookie.getValue();
                    if (value != null && !value.isBlank()) {
                        return value;
                    }
                }
            }
        }

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    private void clearCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(request.isSecure());
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        // SameSite über Header setzen, da Java Cookie API das nicht unterstützt
        response.addHeader("Set-Cookie",
                cookieName + "=; Max-Age=0; Path=/; HttpOnly; SameSite=" + sameSite +
                        (request.isSecure() ? "; Secure" : ""));
    }

    private void sendResponse(HttpServletResponse response, int status, String message) {
        try {
            response.setStatus(status);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"" + message + "\"}");
        } catch (IOException e) {
            log.error("Could not write logout response: {}", e.getMessage());
        }
    }
}