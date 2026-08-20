package com.github.leo51645.assetflow.user.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leo51645.assetflow.security.domain.dto.request.AuthRequestDto;
import com.github.leo51645.assetflow.security.domain.dto.response.AuthResponseDto;
import com.github.leo51645.assetflow.security.domain.entity.Role;
import com.github.leo51645.assetflow.security.service.JwtService;
import com.github.leo51645.assetflow.security.service.RefreshTokenService;
import com.github.leo51645.assetflow.user.domain.dto.mapper.UserDtoMapper;
import com.github.leo51645.assetflow.user.domain.dto.request.DeleteUserRequestDto;
import com.github.leo51645.assetflow.user.domain.dto.request.RegisterRequestDto;
import com.github.leo51645.assetflow.user.domain.dto.request.UpdatePasswordRequestDto;
import com.github.leo51645.assetflow.user.domain.dto.request.UpdateUserRequestDto;
import com.github.leo51645.assetflow.user.domain.dto.response.UserResponseDto;
import com.github.leo51645.assetflow.user.domain.entity.UserEntity;
import com.github.leo51645.assetflow.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserDtoMapper userDtoMapper;

    private RegisterRequestDto registerRequestDtoUserRole;
    private AuthResponseDto authResponseDtoUserRole;

    private UserEntity userEntityAdminRole;
    private AuthResponseDto authResponseDtoAdminRole;

    @Autowired
    UserControllerTest(MockMvc mockMvc, ObjectMapper objectMapper, UserRepository userRepository, JwtService jwtService,
                       RefreshTokenService refreshTokenService, UserDtoMapper userDtoMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userDtoMapper = userDtoMapper;
    }

    @BeforeEach
    void setUp() throws Exception {
        registerRequestDtoUserRole = RegisterRequestDto.builder()
                .email("test123@email.com")
                .password("password123")
                .firstname("Alfred")
                .lastname("Schneider")
                .birthday(LocalDate.of(2000, 8, 18))
                .build();

        String stringResponseUserRole =
                mockMvc.perform(
                        post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequestDtoUserRole))
        )
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        authResponseDtoUserRole = objectMapper.readValue(stringResponseUserRole, AuthResponseDto.class);

        userEntityAdminRole = UserEntity.builder()
                .id(null)
                .email("newTest123@email.com")
                .passwordHash("anotherPassword123")
                .firstName("Alfred2")
                .lastName("Schneider")
                .birthday(LocalDate.of(2000, 8, 17))
                .createdAt(Instant.now())
                .role(Role.ADMIN)
                .build();

        userEntityAdminRole = userRepository.save(userEntityAdminRole);

        String accessToken = jwtService.generateToken(userEntityAdminRole);
        String refreshToken = refreshTokenService.createRefreshToken(userEntityAdminRole);

        authResponseDtoAdminRole = userDtoMapper.toAuthResponseDto(accessToken, refreshToken);
    }

    @Nested
    class GetUser {
        @Test
        void shouldReturnCorrectUserWhenCallingMeEndpoint() throws Exception {
            mockMvc.perform(
                            get("/users/me")
                                    .header("Authorization", "Bearer " + authResponseDtoUserRole.getAccessToken())
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(registerRequestDtoUserRole.getEmail()))
                    .andExpect(jsonPath("$.firstName").value(registerRequestDtoUserRole.getFirstname()))
                    .andExpect(jsonPath("$.lastName").value(registerRequestDtoUserRole.getLastname()))
                    .andExpect(jsonPath("$.birthday").value(registerRequestDtoUserRole.getBirthday().toString()))
                    .andExpect(jsonPath("$.role").value("USER")).toString();
        }

        @Test
        void shouldReturn401DueToInvalidAccessToken() throws Exception {
            UserEntity fakeUser = UserEntity.builder()
                    .id(77L)
                    .email("fake123@email.com")
                    .passwordHash("FakePassword123")
                    .firstName("FakeAlfred")
                    .lastName("NotSchneider")
                    .birthday(LocalDate.of(2000, 8, 16))
                    .createdAt(Instant.now())
                    .role(Role.USER)
                    .build();

            String accessToken = jwtService.generateToken(fakeUser);

            mockMvc.perform(
                    get("/users/me")
                            .header("Authorization", "Bearer " + accessToken)
            ).andExpect(status().isUnauthorized());
        }

        @Test
        void shouldReturnCorrectUserWhenCallingGetAllUser() throws Exception {
            String responseBody = mockMvc.perform(get("/users")
                            .header("Authorization", "Bearer " + authResponseDtoAdminRole.getAccessToken())
            )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            List<UserResponseDto> actualListReturn = objectMapper.readValue(responseBody, new TypeReference<List<UserResponseDto>>() {});

            assertEquals(registerRequestDtoUserRole.getEmail(), actualListReturn.getFirst().email());
            assertEquals(registerRequestDtoUserRole.getFirstname(), actualListReturn.getFirst().firstName());
            assertEquals(registerRequestDtoUserRole.getLastname(), actualListReturn.getFirst().lastName());
            assertEquals(registerRequestDtoUserRole.getBirthday(), actualListReturn.getFirst().birthday());

            assertEquals(userEntityAdminRole.getEmail(), actualListReturn.getLast().email());
            assertEquals(userEntityAdminRole.getFirstName(), actualListReturn.getLast().firstName());
            assertEquals(userEntityAdminRole.getLastName(), actualListReturn.getLast().lastName());
            assertEquals(userEntityAdminRole.getBirthday(), actualListReturn.getLast().birthday());
        }

        @Test
        void shouldNotReturnAllUserDueToAccessDenied() throws Exception {
            mockMvc.perform(get("/users")
                            .header("Authorization", "Bearer " + authResponseDtoUserRole.getAccessToken())
                    )
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }

        @Test
        void shouldGetCorrectUserById() throws Exception {
            mockMvc.perform(
                    get("/users/id/" + userEntityAdminRole.getId())
                            .header("Authorization", "Bearer " + authResponseDtoAdminRole.getAccessToken())
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(userEntityAdminRole.getId()))
                    .andExpect(jsonPath("$.email").value(userEntityAdminRole.getEmail()))
                    .andExpect(jsonPath("$.firstName").value(userEntityAdminRole.getFirstName()))
                    .andExpect(jsonPath("$.lastName").value(userEntityAdminRole.getLastName()))
                    .andExpect(jsonPath("$.birthday").value(userEntityAdminRole.getBirthday().toString()))
                    .andExpect(jsonPath("$.role").value(userEntityAdminRole.getRole().name()));
        }

        @Test
        void shouldNotGetUserByIdDueToAccessDenied() throws Exception {
            mockMvc.perform(
                    get("/users/id/" + userEntityAdminRole.getId())
                            .header("Authorization", "Bearer " + authResponseDtoUserRole.getAccessToken())
            ).andExpect(status().isForbidden());
        }

        @Test
        void shouldNotGetUserByIdDueToMissingAccessToken() throws Exception {
            mockMvc.perform(
                    get("/users/id/" + userEntityAdminRole.getId())
            ).andExpect(status().isUnauthorized());
        }

        @Test
        void shouldGetCorrectUserByEmail() throws Exception {
            mockMvc.perform(
                            get("/users/email/" + userEntityAdminRole.getEmail())
                                    .header("Authorization", "Bearer " + authResponseDtoAdminRole.getAccessToken())
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(userEntityAdminRole.getId()))
                    .andExpect(jsonPath("$.email").value(userEntityAdminRole.getEmail()))
                    .andExpect(jsonPath("$.firstName").value(userEntityAdminRole.getFirstName()))
                    .andExpect(jsonPath("$.lastName").value(userEntityAdminRole.getLastName()))
                    .andExpect(jsonPath("$.birthday").value(userEntityAdminRole.getBirthday().toString()))
                    .andExpect(jsonPath("$.role").value(userEntityAdminRole.getRole().name()));
        }

        @Test
        void shouldNotGetUserByEmailDueToAccessDenied() throws Exception {
            mockMvc.perform(
                    get("/users/email/" + userEntityAdminRole.getEmail())
                            .header("Authorization", "Bearer " + authResponseDtoUserRole.getAccessToken())
            ).andExpect(status().isForbidden());
        }

        @Test
        void shouldNotGetUserByEmailDueToMissingAccessToken() throws Exception {
            mockMvc.perform(
                    get("/users/email/" + userEntityAdminRole.getEmail())
            ).andExpect(status().isUnauthorized());
        }

    }

    @Nested
    class UpdateUser {
        @Test
        void shouldUpdateUser() throws Exception {
            UpdateUserRequestDto updateUserRequestDto = new UpdateUserRequestDto();
            updateUserRequestDto.setEmail("newEmail@email.com");

            mockMvc.perform(
                    patch("/users/me")
                            .header("Authorization", "Bearer " + authResponseDtoUserRole.getAccessToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateUserRequestDto))
            )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(updateUserRequestDto.getEmail()));

        }

        @Test
        void shouldNotUpdateUserDueToInvalidUpdateData() throws Exception {
            UpdateUserRequestDto updateUserRequestDto = new UpdateUserRequestDto();
            updateUserRequestDto.setEmail("invalidEmail");

            mockMvc.perform(
                            patch("/users/me")
                                    .header("Authorization", "Bearer " + authResponseDtoUserRole.getAccessToken())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(updateUserRequestDto))
                    ).andExpect(status().isBadRequest());
        }

        @Test
        void shouldNotUpdateUserDueToInvalidAccessToken() throws Exception {
            UpdateUserRequestDto updateUserRequestDto = new UpdateUserRequestDto();
            updateUserRequestDto.setEmail("test674@email.com");

            mockMvc.perform(
                    patch("/users/me")
                            .header("Authorization", "Bearer " + "invalidAccessToken123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateUserRequestDto))
            ).andExpect(status().isUnauthorized());
        }

        @Test
        void shouldUpdatePassword() throws Exception {
            UpdatePasswordRequestDto passwordRequestDto = UpdatePasswordRequestDto.builder()
                    .oldPassword(registerRequestDtoUserRole.getPassword())
                    .newPassword("newPassword123")
                    .build();

            AuthRequestDto oldAuthRequestDto = AuthRequestDto.builder()
                    .email(registerRequestDtoUserRole.getEmail())
                    .password(registerRequestDtoUserRole.getPassword())
                    .build();

            mockMvc.perform(
                    post("/auth/authenticate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(oldAuthRequestDto))
            ).andExpect(status().isOk());

            mockMvc.perform(
                    patch("/users/me/password")
                            .header("Authorization", "Bearer " + authResponseDtoUserRole.getAccessToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(passwordRequestDto))
            ).andExpect(status().isNoContent());

            AuthRequestDto authSucceedingRequestDto = AuthRequestDto.builder()
                    .email(registerRequestDtoUserRole.getEmail())
                    .password("newPassword123")
                    .build();

            mockMvc.perform(
                    post("/auth/authenticate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(oldAuthRequestDto))
            ).andExpect(status().isUnauthorized());

            mockMvc.perform(
                    post("/auth/authenticate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(authSucceedingRequestDto))
            ).andExpect(status().isOk());
        }

        @Test
        void shouldNotUpdatePasswordDueToInvalidNewPassword() throws Exception {
            UpdatePasswordRequestDto updatePasswordRequestDto = UpdatePasswordRequestDto.builder()
                    .oldPassword(registerRequestDtoUserRole.getPassword())
                    .newPassword("invalid")
                    .build();

            mockMvc.perform(
                    patch("/users/me/password")
                            .header("Authorization", "Bearer " + authResponseDtoUserRole.getAccessToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatePasswordRequestDto))
            ).andExpect(status().isBadRequest());
        }

        @Test
        void shouldNotUpdatePasswordDueToInvalidAccessToken() throws Exception {
            UpdatePasswordRequestDto updatePasswordRequestDto = UpdatePasswordRequestDto.builder()
                    .oldPassword(registerRequestDtoUserRole.getPassword())
                    .newPassword("newPassword123")
                    .build();

            mockMvc.perform(
                    patch("/users/me/password")
                            .header("Authorization", "Bearer " + null)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatePasswordRequestDto))
            ).andExpect(status().isUnauthorized());
        }
    }

    @Nested
    class DeleteUser {
        @Test
        void shouldDeleteUser() throws Exception {
            DeleteUserRequestDto deleteUserRequestDto = DeleteUserRequestDto.builder()
                    .password(registerRequestDtoUserRole.getPassword())
                    .build();

            mockMvc.perform(
                    delete("/users/me")
                            .header("Authorization", "Bearer " + authResponseDtoUserRole.getAccessToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(deleteUserRequestDto))
            ).andExpect(status().isNoContent());

            mockMvc.perform(
                    get("/users/me")
                    .header("Authorization", "Bearer " + authResponseDtoUserRole.getAccessToken())
            ).andExpect(status().isUnauthorized());

            mockMvc.perform(
                    get("/users/email/" + registerRequestDtoUserRole.getEmail())
                            .header("Authorization", "Bearer " + authResponseDtoAdminRole.getAccessToken())
            ).andExpect(status().isNotFound());
        }

        @Test
        void shouldNotDeleteUserDueToInvalidPassword() throws Exception {
            DeleteUserRequestDto deleteUserRequestDto = DeleteUserRequestDto.builder()
                    .password("wrongPassword123")
                    .build();

            mockMvc.perform(
                    delete("/users/me")
                            .header("Authorization", "Bearer " + authResponseDtoUserRole.getAccessToken())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(deleteUserRequestDto))
            ).andExpect(status().isUnauthorized());
        }

        @Test
        void shouldNotDeleteUserDueToInvalidAccessToken() throws Exception {
            DeleteUserRequestDto deleteUserRequestDto = DeleteUserRequestDto.builder()
                    .password(registerRequestDtoUserRole.getPassword())
                    .build();

            mockMvc.perform(
                    delete("/users/me")
                            .header("Authorization", "Bearer " + "invalidAccessToken123")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(deleteUserRequestDto))
            ).andExpect(status().isUnauthorized());
        }

        @Test
        void shouldDeleteUserById() throws Exception {
            String responseBody = mockMvc.perform(get("/users/email/" + registerRequestDtoUserRole.getEmail())
                            .header("Authorization", "Bearer " + authResponseDtoAdminRole.getAccessToken())
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            UserEntity userEntity = objectMapper.readValue(responseBody, UserEntity.class);

            mockMvc.perform(
                    get("/users/me")
                            .header("Authorization", "Bearer " + authResponseDtoUserRole.getAccessToken())
            ).andExpect(status().isOk());

            mockMvc.perform(
                    delete("/users/" + userEntity.getId())
                            .header("Authorization", "Bearer " + authResponseDtoAdminRole.getAccessToken())
            ).andExpect(status().isNoContent());

            mockMvc.perform(
                    get("/users/me")
                            .header("Authorization", "Bearer " + authResponseDtoUserRole.getAccessToken())
            ).andExpect(status().isUnauthorized());
        }

        @Test
        void shouldNotDeleteUserByIdDueToAccessDenied() throws Exception {
            String responseBody = mockMvc.perform(get("/users/email/" + registerRequestDtoUserRole.getEmail())
                            .header("Authorization", "Bearer " + authResponseDtoAdminRole.getAccessToken())
                    )
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString();

            UserEntity userEntity = objectMapper.readValue(responseBody, UserEntity.class);

            mockMvc.perform(
                    delete("/users/" + userEntity.getId())
                            .header("Authorization", "Bearer " + authResponseDtoUserRole.getAccessToken())
            ).andExpect(status().isForbidden());
        }

        @Test
        void shouldReturn404DueToUserNotFound() throws Exception {
            mockMvc.perform(
                    delete("/users/" + userEntityAdminRole.getId() + 9999)
                            .header("Authorization", "Bearer " + authResponseDtoAdminRole.getAccessToken())
            ).andExpect(status().isNotFound());
        }
    }
}