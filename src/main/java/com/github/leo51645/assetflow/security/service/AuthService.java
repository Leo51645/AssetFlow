package com.github.leo51645.assetflow.user.security.auth;

import com.github.leo51645.assetflow.user.domain.dto.mapper.UserDtoMapper;
import com.github.leo51645.assetflow.user.domain.dto.request.RegisterRequestDto;
import com.github.leo51645.assetflow.user.domain.entity.UserEntity;
import com.github.leo51645.assetflow.user.repository.UserRepository;
import com.github.leo51645.assetflow.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
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
