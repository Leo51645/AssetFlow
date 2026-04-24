package com.github.leo51645.assetflow.user.domain.dto.response;

import com.github.leo51645.assetflow.security.domain.entity.Role;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record UserResponseDto(Long id, String email, String firstName, String lastName, LocalDate birthday, LocalDateTime createdAt, Role role) {
}
