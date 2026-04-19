package com.github.leo51645.assetflow.user.domain.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record RegisterResponseDto(Long id, String email, String firstname, String lastname, LocalDate birthday, LocalDateTime createdAt) {
}
