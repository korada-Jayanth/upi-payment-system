package com.upi.upi_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VpaResponseDto {
    private Long id;
    private String vpa;
    private Long userId;
    private Long accountId;
    private String status;
    private LocalDateTime createdAt;
}
