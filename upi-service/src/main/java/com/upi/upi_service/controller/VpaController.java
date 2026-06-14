package com.upi.upi_service.controller;

import com.upi.upi_service.dto.VpaRequestDto;
import com.upi.upi_service.dto.VpaResponseDto;
import com.upi.upi_service.service.VpaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/upi/vpa")
@RequiredArgsConstructor
@Slf4j
public class VpaController {

    private final VpaService vpaService;

    @PostMapping("/create")
    public ResponseEntity<VpaResponseDto> createVpa(
            @Valid @RequestBody VpaRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vpaService.createVpa(request));
    }

    @GetMapping("/{vpa}")
    public ResponseEntity<VpaResponseDto> getVpa(
            @PathVariable String vpa) {
        return ResponseEntity.ok(vpaService.getVpaDetails(vpa));
    }
}
