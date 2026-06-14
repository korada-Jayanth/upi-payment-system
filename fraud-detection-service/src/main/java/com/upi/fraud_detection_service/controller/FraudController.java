package com.upi.fraud_detection_service.controller;

import com.upi.fraud_detection_service.document.FraudCheck;
import com.upi.fraud_detection_service.repository.FraudCheckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fraud")
@RequiredArgsConstructor
public class FraudController {

    private final FraudCheckRepository fraudCheckRepository;

    @GetMapping("/checks")
    public ResponseEntity<List<FraudCheck>> getAllChecks() {
        return ResponseEntity.ok(fraudCheckRepository.findAll());
    }

    @GetMapping("/flagged")
    public ResponseEntity<List<FraudCheck>> getFlagged() {
        return ResponseEntity.ok(fraudCheckRepository.findByFlaggedTrue());
    }

    @GetMapping("/sender/{vpa}")
    public ResponseEntity<List<FraudCheck>> getBySender(@PathVariable String vpa) {
        return ResponseEntity.ok(fraudCheckRepository.findBySenderVpa(vpa));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Fraud Detection Service is running");
    }
}