package com.github.leo51645.assetflow.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.leo51645.assetflow.security.domain.dto.request.AuthRequestDto;
import com.github.leo51645.assetflow.user.domain.dto.request.RegisterRequestDto;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    AuthControllerTest(MockMvc mockMvc, ObjectMapper objectMapper) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Nested
    class Register {
        @Test
        void shouldReturnHttp201AndSetRefreshTokenAtRegistry() throws Exception {
            RegisterRequestDto request =  RegisterRequestDto.builder()
                    .email("test123@email.com")
                    .password("password123")
                    .firstname("Alfred")
                    .lastname("Schneider")
                    .birthday(LocalDate.of(2000, 8, 18))
                    .build();

                    mockMvc.perform(
                            post("/auth/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.accessToken").isNotEmpty())
                    .andExpect(cookie().exists("refreshToken"))
                    .andExpect(cookie().httpOnly("refreshToken", true));
        }

        @Test
        void shouldReturnHttp201AndValidAccessToken() throws Exception {
            RegisterRequestDto registerRequest =  RegisterRequestDto.builder()
                    .email("test076@email.com")
                    .password("password123")
                    .firstname("Alfred")
                    .lastname("Schneider")
                    .birthday(LocalDate.of(2000, 8, 18))
                    .build();

            String responseBody =
                    mockMvc.perform(
                            post("/auth/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(registerRequest))
                            )
                            .andExpect(status().isCreated())
                            .andReturn().getResponse().getContentAsString();

            String accessToken = objectMapper.readTree(responseBody).get("accessToken").asText();

            mockMvc.perform(
                    get("/users/me")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
            )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(registerRequest.getEmail()))
                    .andExpect(jsonPath("$.firstName").value(registerRequest.getFirstname()));
        }

        @Test
        void shouldReturnHttp400DueToInvalidRegisterData() throws Exception {
            RegisterRequestDto request =  RegisterRequestDto.builder()
                    .email("invalidEmail")
                    .password("23p")
                    .firstname("Alfred")
                    .lastname("Schneider")
                    .birthday(LocalDate.of(2000, 8, 18))
                    .build();

            mockMvc.perform(
                            post("/auth/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isBadRequest());
        }

        @Test
        void shouldReturnHttp409DueToEmailAlreadyExists() throws Exception {
            RegisterRequestDto request =  RegisterRequestDto.builder()
                    .email("emailAlreadyExists@email.com")
                    .password("password123")
                    .firstname("Alfred")
                    .lastname("Schneider")
                    .birthday(LocalDate.of(2000, 8, 18))
                    .build();

            mockMvc.perform(
                            post("/auth/register")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                    ).andExpect(status().isCreated());

            mockMvc.perform(
                    post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
            ).andExpect(status().isConflict());
        }
    }

    @Nested
    class Login {
        @Test
        void shouldReturnHttp200AndSetRefreshTokenAtLogin() throws Exception {
            RegisterRequestDto registerRequest =  RegisterRequestDto.builder()
                    .email("test345@mail.com")
                    .password("password123")
                    .firstname("Alfred")
                    .lastname("Schneider")
                    .birthday(LocalDate.of(2000, 8, 18))
                    .build();

            mockMvc.perform(
                    post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest))
            ).andExpect(status().isCreated());

            AuthRequestDto authRequestDto = AuthRequestDto.builder()
                    .email("test345@mail.com")
                    .password("password123")
                    .build();

            mockMvc.perform(
                            post("/auth/authenticate")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(authRequestDto))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accessToken").isNotEmpty())
                    .andExpect(cookie().exists("refreshToken"))
                    .andExpect(cookie().httpOnly("refreshToken", true));
        }

        @Test
        void shouldReturnHttp401DueToInvalidLoginData() throws Exception {
            RegisterRequestDto registerRequest =  RegisterRequestDto.builder()
                    .email("test678@mail.com")
                    .password("password123")
                    .firstname("Alfred")
                    .lastname("Schneider")
                    .birthday(LocalDate.of(2000, 8, 18))
                    .build();

            mockMvc.perform(
                    post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest))
            ).andExpect(status().isCreated());

            AuthRequestDto authRequestDto = AuthRequestDto.builder()
                    .email("wrongEmail345@mail.com")
                    .password("wrongPassword123")
                    .build();

            mockMvc.perform(
                            post("/auth/authenticate")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(authRequestDto))
                    )
                    .andExpect(status().isUnauthorized());
        }

        @Test
        void shouldReturnHttp400DueToBlankPasswordAndEmail() throws Exception {
            RegisterRequestDto registerRequest =  RegisterRequestDto.builder()
                    .email("test234@mail.com")
                    .password("password123")
                    .firstname("Alfred")
                    .lastname("Schneider")
                    .birthday(LocalDate.of(2000, 8, 18))
                    .build();

            mockMvc.perform(
                    post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest))
            ).andExpect(status().isCreated());

            AuthRequestDto authRequestDto = AuthRequestDto.builder()
                    .email("")
                    .password("")
                    .build();

            mockMvc.perform(
                            post("/auth/authenticate")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(authRequestDto))
                    )
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class RefreshToken {
        @Test
        void shouldReturnHttp200AndRotateRefreshTokenLeadingToOldOneGettingInvalid() throws Exception {
            RegisterRequestDto request =  RegisterRequestDto.builder()
                    .email("email098@email.com")
                    .password("password123")
                    .firstname("Alfred")
                    .lastname("Schneider")
                    .birthday(LocalDate.of(2000, 8, 18))
                    .build();

            Cookie refreshToken = mockMvc.perform(
                    post("/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
            )
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getCookie( "refreshToken");

            assert refreshToken != null;

            mockMvc.perform(
                    post("/auth/refreshToken")
                            .cookie(refreshToken)
            )
                    .andExpect(status().isOk())
                    .andExpect(cookie().exists("refreshToken"))
                    .andExpect(cookie().value("refreshToken", not(equalTo(refreshToken.getValue()))))
                    .andExpect(jsonPath("$.accessToken").isNotEmpty());

            mockMvc.perform(
                    post("/auth/refreshToken")
                            .cookie(refreshToken)
            ).andExpect(status().isUnauthorized());
        }

        @Test
        void shouldReturnHttp401DueToRefreshTokenEqualToNull() throws Exception {
            mockMvc.perform(
                    post("/auth/refreshToken")
                            .cookie(new Cookie("refreshToken", null))
            ).andExpect(status().isUnauthorized());
        }

        @Test
        void shouldReturnHttp401DueToEmptyRefreshToken() throws Exception {
            mockMvc.perform(
                    post("/auth/refreshToken")
                            .cookie(new Cookie("refreshToken", ""))
            ).andExpect(status().isUnauthorized());
        }
    }
}