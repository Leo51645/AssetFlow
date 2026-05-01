package com.github.leo51645.assetflow.user.controller;

import com.github.leo51645.assetflow.user.domain.dto.response.UserResponseDto;
import com.github.leo51645.assetflow.user.domain.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(@AuthenticationPrincipal UserEntity userEntity) {
        UserResponseDto responseDto = UserResponseDto.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .firstName(userEntity.getFirstname())
                .lastName(userEntity.getLastname())
                .birthday(userEntity.getBirthday())
                .createdAt(userEntity.getCreatedAt())
                .role(userEntity.getRole())
                .build();
        return ResponseEntity.ok(responseDto);
    }

}
