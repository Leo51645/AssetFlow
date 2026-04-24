package com.github.leo51645.assetflow.security.controller;

import com.github.leo51645.assetflow.security.domain.dto.response.AuthResponseDto;
import com.github.leo51645.assetflow.security.service.AuthService;
import com.github.leo51645.assetflow.user.domain.dto.request.RegisterRequestDto;
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

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody @Valid RegisterRequestDto request, HttpServletResponse response) {
        AuthResponseDto responseDto = authService.register(request);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

}
