package com.github.leo51645.assetflow.trade_transaction.domain.dto.request;


import jakarta.validation.constraints.Min;
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
public class OrderRequestDto {
    @NotBlank(message = "Asset name or ISIN is required")
    @Size(max = 255)
    String assetIdentifierParameter;

    @NotBlank(message = "Amount is required")
    @Min(value = 1, message = "Quantity cannot be less than 1")
    Long quantity;
}
