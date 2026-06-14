package com.upi.upi_service.service;

import com.upi.upi_service.client.AccountServiceClient;
import com.upi.upi_service.client.UserServiceClient;
import com.upi.upi_service.dto.VpaRequestDto;
import com.upi.upi_service.dto.VpaResponseDto;
import com.upi.upi_service.entity.VirtualPaymentAddress;
import com.upi.upi_service.entity.VpaStatus;
import com.upi.upi_service.exception.VpaNotFoundException;
import com.upi.upi_service.repository.VpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class VpaService {

    private final VpaRepository vpaRepository;
    private final UserServiceClient userServiceClient;
    private final AccountServiceClient accountServiceClient;
    private final PasswordEncoder passwordEncoder;

    public VpaResponseDto createVpa(VpaRequestDto request) {
        // 1. Validate user exists
        userServiceClient.getUserById(request.getUserId());

        // 2. Validate account exists
        accountServiceClient.getAccountById(request.getAccountId());

        // 3. Check VPA not already taken
        if (vpaRepository.existsByVpa(request.getVpa())) {
            throw new IllegalArgumentException("VPA already taken: " + request.getVpa());
        }

        // 4. Hash the PIN before storing — never store raw PIN
        String hashedPin = passwordEncoder.encode(request.getPin());

        VirtualPaymentAddress vpa = VirtualPaymentAddress.builder()
                .vpa(request.getVpa())
                .userId(request.getUserId())
                .accountId(request.getAccountId())
                .hashedPin(hashedPin)
                .status(VpaStatus.ACTIVE)
                .build();

        VirtualPaymentAddress saved = vpaRepository.save(vpa);
        log.info("VPA created: {}", saved.getVpa());
        return mapToResponse(saved);
    }

    public VpaResponseDto getVpaDetails(String vpa) {
        VirtualPaymentAddress found = vpaRepository.findByVpa(vpa)
                .orElseThrow(() -> new VpaNotFoundException("VPA not found: " + vpa));
        return mapToResponse(found);
    }

    private VpaResponseDto mapToResponse(VirtualPaymentAddress vpa) {
        return VpaResponseDto.builder()
                .id(vpa.getId())
                .vpa(vpa.getVpa())
                .userId(vpa.getUserId())
                .accountId(vpa.getAccountId())
                .status(vpa.getStatus().name())
                .createdAt(vpa.getCreatedAt())
                .build();
    }
}
