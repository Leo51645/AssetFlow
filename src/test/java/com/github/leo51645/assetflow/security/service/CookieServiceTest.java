package com.github.leo51645.assetflow.security.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CookieServiceTest {

    CookieService cookieService = new CookieService();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cookieService, "refreshTokenExpirationMs", 604800000L);
        ReflectionTestUtils.setField(cookieService, "cookiePath", "/api/auth");
        ReflectionTestUtils.setField(cookieService, "secureCookie", true);
        ReflectionTestUtils.setField(cookieService, "sameSite", "Strict");
    }

    @Test
    void shouldExtractRefreshTokenFromCookie() {
        Cookie cookie = new Cookie(CookieService.REFRESH_TOKEN_COOKIE_NAME, "refreshToken123");
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        String result = cookieService.extractRefreshTokenFromCookie(request);

        assertEquals("refreshToken123", result);
    }

    @Test
    void shouldReturnNullWhenRequestDoesNotContainRefreshTokenCookie() {
        Cookie cookie = new Cookie("cookie-name", "refreshToken123");
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getCookies()).thenReturn(new Cookie[]{cookie});

        String result = cookieService.extractRefreshTokenFromCookie(request);

        assertNull(result);
    }

    @Test
    void shouldReturnNullWhenRequestDoesNotContainAnyCookies() {
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(request.getCookies()).thenReturn(null);

        String result = cookieService.extractRefreshTokenFromCookie(request);

        assertNull(result);
    }

    @Test
    void shouldAddRefreshTokenToCookie() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);

        cookieService.addRefreshTokenCookie(response, "refreshToken123");

        verify(response).addCookie(cookieCaptor.capture());
        Cookie actualCookie = cookieCaptor.getValue();

        assertNotNull(actualCookie);
        assertEquals("refreshToken123", actualCookie.getValue());
        assertTrue(actualCookie.isHttpOnly());
        assertTrue(actualCookie.getSecure());
        assertEquals("/api/auth", actualCookie.getPath());
        assertEquals("Strict", actualCookie.getAttribute("SameSite"));
        assertEquals(604800, actualCookie.getMaxAge());
    }

}