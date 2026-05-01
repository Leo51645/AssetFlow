package com.github.leo51645.assetflow.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leo51645.assetflow.security.exceptionHandling.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        String errorId = UUID.randomUUID().toString();

        log.warn("Access denied error: {}", accessDeniedException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        final ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpServletResponse.SC_FORBIDDEN)
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message(accessDeniedException.getMessage())
                .path(request.getRequestURI())
                .errorId(errorId)
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
