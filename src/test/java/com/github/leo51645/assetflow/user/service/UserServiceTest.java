package com.github.leo51645.assetflow.user.service;

import com.github.leo51645.assetflow.security.domain.entity.Role;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserDtoMapper userDtoMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserUtility userUtility;
    @Mock
    private RefreshTokenService  refreshTokenService;
    @InjectMocks
    private UserService userService;

    @Nested
    class CreateUser {
        @Test
        void shouldCreateUser() {
            RegisterRequestDto request =  new RegisterRequestDto();
            UserEntity userEntity = new UserEntity();
            UserEntity expectedUserResponse = UserEntity.builder()
                    .id(1L)
                    .email("random@email.com")
                    .passwordHash("password123")
                    .firstname("Alfred")
                    .lastname("Schneider")
                    .birthday(LocalDate.of(2000, 8, 18))
                    .createdAt(Instant.now())
                    .role(Role.USER)
                    .build();


            when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
            when(userDtoMapper.toUserEntity(request, passwordEncoder)).thenReturn(userEntity);
            when(userRepository.save(userEntity)).thenReturn(expectedUserResponse);

            UserEntity actualUserResponse = userService.createUser(request);

            assertEquals(expectedUserResponse, actualUserResponse);
            verify(userRepository).existsByEmail(request.getEmail());
            verify(userDtoMapper).toUserEntity(request, passwordEncoder);
            verify(userRepository).save(userEntity);


        }

        @Test
        void shouldNotCreateUserDueToEmailAlreadyExists() {
            RegisterRequestDto request = new RegisterRequestDto();
            request.setEmail("testEmail@mail.com");

            when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

            assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(request));
            verify(userRepository).existsByEmail(request.getEmail());
            verify(userDtoMapper, never()).toUserEntity(any(RegisterRequestDto.class), any(PasswordEncoder.class));
            verify(userRepository, never()).save(any(UserEntity.class));
        }
    }

    @Nested
    class GetUser {
        @Test
        void shouldGetUserById() {
            UserEntity userEntity = new UserEntity();

            when(userRepository.findById(userEntity.getId())).thenReturn(Optional.of(userEntity));

            UserEntity actualUser = userService.getUserById(userEntity.getId());

            assertEquals(userEntity, actualUser);
            verify(userRepository).findById(userEntity.getId());
        }

        @Test
        void shouldNotGetUserDueToUserNotFoundById() {
            Long userId = 1L;

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
            verify(userRepository).findById(userId);
        }

        @Test
        void shouldGetUserByEmail() {
            UserEntity userEntity = new UserEntity();
            userEntity.setEmail("test@mail.com");

            when(userRepository.findByEmail(userEntity.getEmail())).thenReturn(Optional.of(userEntity));

            UserEntity actualUser = userService.getUserByEmail(userEntity.getEmail());

            assertEquals(userEntity, actualUser);
            verify(userRepository).findByEmail(userEntity.getEmail());
        }

        @Test
        void shouldNotGetUserDueToUserNotFoundByEmail() {
            String email = "test@email.com";

            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail(email));
            verify(userRepository).findByEmail(email);
        }

        @Test
        void shouldReturnAllUser() {
            userService.getAllUsers();
            verify(userRepository).findAll();
        }
    }

    @Nested
    class UpdateUser {
        @Test
        void shouldUpdateUser() {
            UpdateUserRequestDto request = new UpdateUserRequestDto();
            request.setEmail("test@email.com");
            request.setFirstname("Alfred2");
            UserEntity oldUserEntity = UserEntity.builder()
                    .id(1L)
                    .email("test@email.com")
                    .passwordHash("password123")
                    .firstname("Alfred")
                    .lastname("Schneider")
                    .birthday(LocalDate.of(2000, 8, 18))
                    .createdAt(Instant.now())
                    .role(Role.USER)
                    .build();

            UserEntity expectedUser = UserEntity.builder()
                    .id(1L)
                    .email("test@email.com")
                    .passwordHash("password123")
                    .firstname(request.getFirstname())
                    .lastname("Schneider")
                    .birthday(LocalDate.of(2000, 8, 18))
                    .createdAt(Instant.now())
                    .role(Role.USER)
                    .build();

            when(userRepository.findById(oldUserEntity.getId())).thenReturn(Optional.of(oldUserEntity));
            when(userRepository.save(oldUserEntity)).thenReturn(expectedUser);

            UserEntity actualUser = userService.updateUser(oldUserEntity.getId(), request);

            assertEquals(expectedUser, actualUser);
            verify(userRepository).save(any(UserEntity.class));
        }

        @Test
        void shouldNotUpdateUserDueToUserNotFoundById() {
            UpdateUserRequestDto request = new UpdateUserRequestDto();
            Long userId = 1L;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, request));
            verify(userRepository, never()).save(any(UserEntity.class));
        }

        @Test
        void shouldNotUpdateEmailFieldDueToEmailAlreadyExists() {
            UpdateUserRequestDto request = new UpdateUserRequestDto();
            request.setEmail("test@email.com");
            UserEntity userEntity = new UserEntity();
            userEntity.setEmail("another@email.com");
            Long userId = 1L;

            when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
            when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

            assertThrows(EmailAlreadyExistsException.class, () -> userService.updateUser(userId, request));
        }

        @Test
        void shouldUpdatePassword() {
            long oldUserId = 1L;
            UpdatePasswordRequestDto request = new UpdatePasswordRequestDto();
            UserEntity oldUserEntity = new UserEntity();
            String oldPasswordHash = oldUserEntity.getPasswordHash();

            when(userRepository.findById(oldUserId)).thenReturn(Optional.of(oldUserEntity));
            when(passwordEncoder.matches(request.getOldPassword(), oldPasswordHash)).thenReturn(true);
            when(passwordEncoder.encode(request.getNewPassword())).thenReturn("newPassword123");

            userService.updatePassword(oldUserId, request);

            verify(userRepository).findById(oldUserId);
            verify(passwordEncoder).matches(request.getOldPassword(), oldPasswordHash);
            verify(passwordEncoder).encode(request.getNewPassword());
            verify(userRepository).save(any(UserEntity.class));
        }

        @Test
        void shouldNotUpdatePasswordDueToUserNotFoundById() {
            long oldUserId = 1L;
            UpdatePasswordRequestDto request = new UpdatePasswordRequestDto();
            UserEntity oldUserEntity = new UserEntity();

            when(userRepository.findById(oldUserId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.updatePassword(oldUserId, request));
            verify(passwordEncoder, never()).matches(request.getOldPassword(), oldUserEntity.getPasswordHash());
            verify(passwordEncoder, never()).encode(request.getNewPassword());
            verify(userRepository, never()).save(any(UserEntity.class));
        }

        @Test
        void shouldNotUpdatePasswordDueToInvalidPassword() {
            UpdatePasswordRequestDto request = new UpdatePasswordRequestDto();
            UserEntity oldUserEntity = new UserEntity();
            long oldUserId = 1L;

            when(userRepository.findById(oldUserId)).thenReturn(Optional.of(oldUserEntity));
            when(passwordEncoder.matches(request.getOldPassword(), oldUserEntity.getPasswordHash())).thenReturn(false);

            assertThrows(InvalidPasswordException.class, () -> userService.updatePassword(oldUserId, request));
            verify(passwordEncoder, never()).encode(request.getNewPassword());
            verify(userRepository, never()).save(any(UserEntity.class));
        }
    }

    @Nested
    class DeleteUser {
        @Test
        void shouldDeleteUserById() {
            UserEntity userEntity = new UserEntity();
            long userId = 1L;

            when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

            userService.deleteUserById(userId);

            verify(refreshTokenService).deleteAllTokensByUser(userEntity);
            verify(userRepository).delete(userEntity);
        }

        @Test
        void shouldDeleteUser() {
            DeleteUserRequestDto request = new DeleteUserRequestDto();
            long userId = 1L;
            UserEntity userEntity = new UserEntity();

            when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
            when(passwordEncoder.matches(request.getPassword(), userEntity.getPasswordHash())).thenReturn(true);

            userService.deleteUser(userId, request);

            verify(refreshTokenService).deleteAllTokensByUser(userEntity);
            verify(userRepository).delete(userEntity);
        }

        @Test
        void shouldNotDeleteUserDueToUserNotFoundById() {
            long userId = 1L;
            DeleteUserRequestDto request = new DeleteUserRequestDto();

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId, request));
        }

        @Test
        void shouldNotDeleteUserDueToInvalidPassword() {
            DeleteUserRequestDto request = new DeleteUserRequestDto();
            long userId = 1L;
            UserEntity userEntity = new UserEntity();

            when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
            when(passwordEncoder.matches(request.getPassword(), userEntity.getPasswordHash())).thenReturn(false);

            assertThrows(InvalidPasswordException.class, () -> userService.deleteUser(userId, request));
        }
    }


}