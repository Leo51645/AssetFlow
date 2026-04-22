package com.github.leo51645.assetflow.user.service;

import com.github.leo51645.assetflow.user.domain.dto.mapper.UserDtoMapper;
import com.github.leo51645.assetflow.user.domain.dto.request.RegisterRequestDto;
import com.github.leo51645.assetflow.user.domain.dto.request.UpdateUserRequestDto;
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

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;
    private UserDtoMapper userDtoMapper;
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserEntity createUser(@Valid RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        UserEntity userEntity = userDtoMapper.toUserEntity(request, passwordEncoder);
        UserEntity savedUser = userRepository.save(userEntity);
        log.info("User with email {} registered successfully", request.getEmail());
        return savedUser;
    }

    @Transactional
    public UserEntity getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    @Transactional
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
    }

    @Transactional
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public UserEntity updateUser(Long id_oldUser, @Valid UpdateUserRequestDto request) {

        UserEntity oldUserEntity = userRepository.findById(id_oldUser)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id_oldUser + " not found"));

        if (request.getEmail() != null) {
            if (!oldUserEntity.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
            }
            oldUserEntity.setEmail(request.getEmail());
        }
        if (request.getFirstname() != null) {
            oldUserEntity.setFirstname(request.getFirstname());
        }
        if (request.getLastname() != null) {
            oldUserEntity.setLastname(request.getLastname());
        }
        if (request.getBirthday() != null) {
            oldUserEntity.setBirthday(request.getBirthday());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            oldUserEntity.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        UserEntity updatedUser = userRepository.save(oldUserEntity);
        log.info("User with id {} updated successfully", id_oldUser);
        return updatedUser;
    }

    @Transactional
    public void deleteUser(UserEntity user) {
        if(!userRepository.existsById(user.getId())) {
            throw new UserNotFoundException("User with id " + user.getId() + " not found");
        }
        userRepository.delete(user);
    }

}