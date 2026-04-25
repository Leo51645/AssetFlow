package com.github.leo51645.assetflow.security.controller;

import com.github.leo51645.assetflow.security.domain.dto.request.AuthRequestDto;
import com.github.leo51645.assetflow.security.domain.dto.response.AuthResponseDto;
import com.github.leo51645.assetflow.security.service.AuthService;
import com.github.leo51645.assetflow.security.service.CookieService;
import com.github.leo51645.assetflow.user.domain.dto.request.RegisterRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody @Valid RegisterRequestDto request, HttpServletResponse response) {
        AuthResponseDto responseDto = authService.register(request);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDto> authenticate(
            @Valid @RequestBody AuthRequestDto request,
            HttpServletResponse response
    ) {
        AuthResponseDto responseDto = authService.authenticate(request);
        cookieService.addRefreshTokenCookie(response, responseDto.getRefreshToken());

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponseDto> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = cookieService.extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AuthResponseDto responseDto = authService.refreshToken(refreshToken);
        cookieService.addRefreshTokenCookie(response, responseDto.getRefreshToken());

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String refreshToken = cookieService.extractRefreshTokenFromCookie(request);
        authService.logout(refreshToken);
        cookieService.clearRefreshTokenCookie(response);

        return ResponseEntity.ok().build();
    }

}
