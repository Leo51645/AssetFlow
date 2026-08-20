package com.github.leo51645.assetflow.user.service;

import com.github.leo51645.assetflow.security.service.RefreshTokenService;
import com.github.leo51645.assetflow.user.domain.dto.mapper.UserDtoMapper;
import com.github.leo51645.assetflow.user.domain.dto.request.DeleteUserRequestDto;
import com.github.leo51645.assetflow.user.domain.dto.request.RegisterRequestDto;
import com.github.leo51645.assetflow.user.domain.dto.request.UpdatePasswordRequestDto;
import com.github.leo51645.assetflow.user.domain.dto.request.UpdateUserRequestDto;
import com.github.leo51645.assetflow.user.domain.entity.UserEntity;
import com.github.leo51645.assetflow.user.exception.EmailAlreadyExistsException;
import com.github.leo51645.assetflow.user.exception.InvalidPasswordException;
import com.github.leo51645.assetflow.user.exception.UserNotFoundException;
import com.github.leo51645.assetflow.user.repository.UserRepository;
import com.github.leo51645.assetflow.user.util.UserUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserUtility userUtility;
    private final RefreshTokenService  refreshTokenService;

    @Transactional
    public UserEntity createUser(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + userUtility.maskEmail(request.getEmail()));
        }

        UserEntity userEntity = userDtoMapper.toUserEntity(request, passwordEncoder);
        UserEntity savedUser = userRepository.save(userEntity);
        log.info("User with email {} registered successfully", userUtility.maskEmail(request.getEmail()));
        return savedUser;
    }

    @Transactional(readOnly = true)
    public UserEntity getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public UserEntity getUserByEmail(String email) {
        String maskedEmail = userUtility.maskEmail(email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + maskedEmail + " not found"));
    }

    @Transactional(readOnly = true)
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public UserEntity updateUser(Long id_oldUser, UpdateUserRequestDto request) {

        UserEntity oldUserEntity = userRepository.findById(id_oldUser)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id_oldUser + " not found"));

        if (request.getEmail() != null) {
            if (!oldUserEntity.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new EmailAlreadyExistsException("Email already exists: " + userUtility.maskEmail(request.getEmail()));
            }
            oldUserEntity.setEmail(request.getEmail());
        }
        if (request.getFirstname() != null) {
            oldUserEntity.setFirstName(request.getFirstname());
        }
        if (request.getLastname() != null) {
            oldUserEntity.setLastName(request.getLastname());
        }
        if (request.getBirthday() != null) {
            oldUserEntity.setBirthday(request.getBirthday());
        }

        UserEntity updatedUser = userRepository.save(oldUserEntity);
        log.info("User with id {} updated successfully", id_oldUser);
        return updatedUser;
    }

    @Transactional
    public void updatePassword(Long oldUserId, UpdatePasswordRequestDto request) {
        UserEntity oldUserEntity = userRepository.findById(oldUserId)
                .orElseThrow(() -> new UserNotFoundException("User with id " + oldUserId + " not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), oldUserEntity.getPasswordHash())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        oldUserEntity.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(oldUserEntity);
        log.info("Password from User with id {} updated successfully", oldUserId);
    }

    @Transactional
    public void deleteUserById(Long id) {
        UserEntity userEntity = getUserById(id);
        refreshTokenService.deleteAllTokensByUser(userEntity);
        userRepository.delete(userEntity);
        log.info("User with id {} was successfully deleted by ADMIN", id);
    }

    @Transactional
    public void deleteUser(Long id, DeleteUserRequestDto request) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        if (!passwordEncoder.matches(request.getPassword(), userEntity.getPasswordHash())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        refreshTokenService.deleteAllTokensByUser(userEntity);
        userRepository.delete(userEntity);
        log.info("User with id {} deleted successfully", id);
    }

}