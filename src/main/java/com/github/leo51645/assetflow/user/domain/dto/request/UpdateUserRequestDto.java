package com.github.leo51645.assetflow.user.domain.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequestDto {
    @Email(message = "Invalid email")
    @Size(max = 255)
    private String email;

    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String password;

    @Size(min = 1, max = 30, message = "First name must be between 1 and 30 characters")
    private String firstname;

    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    private String lastname;

    @Past(message = "Invalid birthdate")
    private LocalDate birthday;
}
