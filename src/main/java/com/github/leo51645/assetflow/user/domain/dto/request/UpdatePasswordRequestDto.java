package com.github.leo51645.assetflow.user.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordRequestDto {

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Old password must have been between 8 and 255 characters")
    private String oldPassword;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "New password must be between 8 and 255 characters")
    private String newPassword;
}
