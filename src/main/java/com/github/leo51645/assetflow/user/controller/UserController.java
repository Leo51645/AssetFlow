package com.github.leo51645.assetflow.user.controller;

import com.github.leo51645.assetflow.user.domain.dto.mapper.UserDtoMapper;
import com.github.leo51645.assetflow.user.domain.dto.request.UpdatePasswordRequestDto;
import com.github.leo51645.assetflow.user.domain.dto.request.UpdateUserRequestDto;
import com.github.leo51645.assetflow.user.domain.dto.response.UserResponseDto;
import com.github.leo51645.assetflow.user.domain.entity.UserEntity;
import com.github.leo51645.assetflow.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserDtoMapper userDtoMapper;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(@AuthenticationPrincipal UserEntity userEntity) {
        return ResponseEntity.ok(userDtoMapper.toUserResponseDto(userEntity));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponseDto>> getAllUser() {
        List<UserResponseDto> mappedList = userService.getAllUsers()
                .stream()
                .map(userDtoMapper::toUserResponseDto)
                .toList();
        return ResponseEntity.ok(mappedList);
    }

    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userDtoMapper.toUserResponseDto(userService.getUserById(id)));
    }

    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userDtoMapper.toUserResponseDto(userService.getUserByEmail(email)));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponseDto> updateUser(@RequestBody @Valid UpdateUserRequestDto updateUserRequestDto, @AuthenticationPrincipal UserEntity userEntity) {
        return ResponseEntity.ok(userDtoMapper.toUserResponseDto(userService.updateUser(userEntity.getId(), updateUserRequestDto)));
    }

    @PatchMapping("/me/password")
    public ResponseEntity updatePassword(@RequestBody @Valid UpdatePasswordRequestDto updatePasswordRequestDto,
                                                          @AuthenticationPrincipal UserEntity userEntity) {
        userService.
        return ResponseEntity.ok("");
    }
}
