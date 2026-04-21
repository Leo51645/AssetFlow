package com.github.leo51645.assetflow.user.service;

import com.github.leo51645.assetflow.user.domain.dto.mapper.UserDtoMapper;
import com.github.leo51645.assetflow.user.domain.dto.request.RegisterRequestDto;
import com.github.leo51645.assetflow.user.domain.dto.response.UserResponseDto;
import com.github.leo51645.assetflow.user.domain.entity.UserEntity;
import com.github.leo51645.assetflow.user.exception.EmailAlreadyExistsException;
import com.github.leo51645.assetflow.user.exception.UserNotFoundException;
import com.github.leo51645.assetflow.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private UserDtoMapper userDtoMapper;
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponseDto createUser(@Valid RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        UserEntity userEntity = userDtoMapper.toUserEntity(request, passwordEncoder);
        UserEntity savedUser = userRepository.save(userEntity);
        log.info("User with email {} registered successfully", request.getEmail());
        return userDtoMapper.toUserResponseDto(savedUser);
    }

    @Transactional
    public UserResponseDto getUserById(UserEntity user) {
        Optional<UserEntity> userEntity = userRepository.findById(user.getId());
        if (userEntity.isEmpty()) {
            throw new UserNotFoundException("User with id " + user.getId() + " not found");
        } else {
            return userDtoMapper.toUserResponseDto(userEntity.get());
        }
    }

    @Transactional
    public UserResponseDto getUserByEmail(UserEntity user) {
        Optional<UserEntity> userEntity = userRepository.findByEmail(user.getEmail());
        if (userEntity.isEmpty()) {
            throw new UserNotFoundException("User with email " + user.getEmail() + " not found");
        } else {
            return userDtoMapper.toUserResponseDto(userEntity.get());
        }
    }

    @Transactional
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> userDtoMapper.toUserResponseDto(user))
                .collect(Collectors.toList());
    }

    // TODO: Update User CRUD Functionality

    @Transactional
    public void deleteUser(UserEntity user) {
        if(!userRepository.existsByEmail(user.getEmail())) {
            throw new UserNotFoundException("User with email " + user.getEmail() + " not found");
        }
        userRepository.delete(user);
    }

}