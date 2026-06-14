package com.upi.upi_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDto {

    @NotBlank(message = "Sender VPA is required")
    private String senderVpa;        // jayanth@upi

    @NotBlank(message = "Receiver VPA is required")
    private String receiverVpa;      // friend@upi

    @NotNull
    @DecimalMin(value = "1.0", message = "Minimum transfer is ₹1")
    private BigDecimal amount;

    @NotBlank(message = "PIN is required")
    private String pin;

    private String remarks;          // optional note
}
