package com.github.leo51645.assetflow.user.domain.dto.mapper;

import com.github.leo51645.assetflow.user.domain.dto.request.RegisterRequestDto;
import com.github.leo51645.assetflow.security.domain.dto.response.AuthResponseDto;
import com.github.leo51645.assetflow.user.domain.dto.response.UserResponseDto;
import com.github.leo51645.assetflow.user.domain.entity.UserEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {

    public UserResponseDto toUserResponseDto(UserEntity userEntity) {
        return UserResponseDto.builder()
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .firstName(userEntity.getFirstname())
                .lastName(userEntity.getLastname())
                .birthday(userEntity.getBirthday())
                .createdAt(userEntity.getCreatedAt())
                .build();
    }

    public AuthResponseDto toAuthResponseDto(UserEntity userEntity, String accessToken, String refreshToken) {
        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .id(userEntity.getId())
                .email(userEntity.getEmail())
                .build();
    }

    public UserEntity toUserEntity(RegisterRequestDto request, PasswordEncoder passwordEncoder) {
        return UserEntity.builder().email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .birthday(request.getBirthday())
                .build();
    }

}
