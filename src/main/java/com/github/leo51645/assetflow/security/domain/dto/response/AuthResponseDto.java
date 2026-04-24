package com.github.leo51645.assetflow.security.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;


@Builder
public class AuthResponseDto {
    private String accessToken;

    @JsonIgnore
    private String refreshToken;

    private Long id;

    private String email;
}
