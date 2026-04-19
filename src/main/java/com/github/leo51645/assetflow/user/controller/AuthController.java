package com.github.leo51645.assetflow.user.controller;

import com.github.leo51645.assetflow.user.domain.dto.request.RegisterRequestDto;
import com.github.leo51645.assetflow.user.domain.dto.response.RegisterResponseDto;
import com.github.leo51645.assetflow.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public ResponseEntity<RegisterResponseDto> register(@RequestBody @Valid RegisterRequestDto request, HttpServletResponse response) {
        RegisterResponseDto responseDto = userService.register(request);

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

}
