package com.upi.upi_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VpaRequestDto {

    @NotNull
    private Long userId;

    @NotNull
    private Long accountId;

    @NotBlank(message = "VPA handle required e.g. jayanth@upi")
    private String vpa;

    @NotBlank
    @Size(min = 4, max = 6, message = "PIN must be 4-6 digits")
    private String pin;
}
