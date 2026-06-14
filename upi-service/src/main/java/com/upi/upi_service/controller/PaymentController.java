package com.upi.upi_service.controller;

import com.upi.upi_service.dto.PaymentRequestDto;
import com.upi.upi_service.dto.PaymentResponseDto;
import com.upi.upi_service.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/upi")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<PaymentResponseDto> initiatePayment(
            @Valid @RequestBody PaymentRequestDto request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(paymentService.initiatePayment(request));
    }
}
