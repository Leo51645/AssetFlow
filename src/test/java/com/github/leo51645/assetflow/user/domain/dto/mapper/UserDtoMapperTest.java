package com.github.leo51645.assetflow.user.domain.dto.mapper;

import com.github.leo51645.assetflow.security.domain.dto.response.AuthResponseDto;
import com.github.leo51645.assetflow.security.domain.entity.Role;
import com.github.leo51645.assetflow.user.domain.dto.request.RegisterRequestDto;
import com.github.leo51645.assetflow.user.domain.dto.response.UserResponseDto;
import com.github.leo51645.assetflow.user.domain.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserDtoMapperTest {

    UserDtoMapper userDtoMapper = new UserDtoMapper();
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void shouldParseUserEntityToUserResponseDto() {
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .email("test123@email.com")
                .passwordHash("password123")
                .firstName("Alfred")
                .lastName("Schneider")
                .birthday(LocalDate.of(2000, 8, 18))
                .createdAt(Instant.now())
                .role(Role.USER)
                .build();

        UserResponseDto userResponseDto = userDtoMapper.toUserResponseDto(userEntity);

        assertEquals(userEntity.getId(), userResponseDto.id());
        assertEquals(userEntity.getEmail(), userResponseDto.email());
        assertEquals(userEntity.getFirstName(), userResponseDto.firstName());
        assertEquals(userEntity.getLastName(), userResponseDto.lastName());
        assertEquals(userEntity.getBirthday(), userResponseDto.birthday());
        assertEquals(userEntity.getCreatedAt(), userResponseDto.createdAt());
        assertEquals(userEntity.getRole(), userResponseDto.role());
    }

    @Test
    void shouldParseAccessAndRefreshTokenToAuthResponseDto() {
        String accessToken = "accessToken123";
        String refreshToken = "refreshToken123";

        AuthResponseDto authResponseDto = userDtoMapper.toAuthResponseDto(accessToken, refreshToken);

        assertEquals(accessToken, authResponseDto.getAccessToken());
        assertEquals(refreshToken, authResponseDto.getRefreshToken());
    }

    @Test
    void shouldParseRegisterRequestDtoToUserEntity() {
        RegisterRequestDto registerRequestDto = RegisterRequestDto.builder()
                .email("test123@email.com")
                .password("password123")
                .firstname("Alfred")
                .lastname("Schneider")
                .birthday(LocalDate.of(2000, 8, 18))
                .build();

        UserEntity userEntity = userDtoMapper.toUserEntity(registerRequestDto, passwordEncoder);

        assertEquals(registerRequestDto.getEmail(), userEntity.getEmail());
        assertTrue(passwordEncoder.matches(registerRequestDto.getPassword(), userEntity.getPassword()));
        assertEquals(registerRequestDto.getFirstname(), userEntity.getFirstName());
        assertEquals(registerRequestDto.getLastname(), userEntity.getLastName());
        assertEquals(registerRequestDto.getBirthday(), userEntity.getBirthday());
        assertEquals(Role.USER, userEntity.getRole());
    }

}