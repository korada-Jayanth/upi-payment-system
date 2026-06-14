package com.upi.account_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponseDto {
    private Long id;
    private String accountNumber;
    private String bankName;
    private String ifscCode;
    private BigDecimal balance;
    private String status;
    private Long userId;
    private String upiId;
    private LocalDateTime createdAt;
}
