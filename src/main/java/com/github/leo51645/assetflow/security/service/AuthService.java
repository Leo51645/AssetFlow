package com.github.leo51645.assetflow.security.service;

import com.github.leo51645.assetflow.security.domain.dto.request.AuthRequestDto;
import com.github.leo51645.assetflow.security.domain.dto.response.AuthResponseDto;
import com.github.leo51645.assetflow.user.domain.dto.mapper.UserDtoMapper;
import com.github.leo51645.assetflow.user.domain.dto.request.RegisterRequestDto;
import com.github.leo51645.assetflow.user.domain.entity.UserEntity;
import com.github.leo51645.assetflow.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final UserDtoMapper userDtoMapper;

    @Transactional
    public AuthResponseDto register(RegisterRequestDto request) {

        UserEntity userEntity = userService.createUser(request);

        return userDtoMapper.toAuthResponseDto(userEntity,null, null); // TODO: JWT hier einfügen
    }

    @Transactional
    public AuthResponseDto authenticate(AuthRequestDto request) {

    }
}
