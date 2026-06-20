package com.upi.upi_service.service;

import com.upi.upi_service.client.AccountServiceClient;
import com.upi.upi_service.dto.PaymentEventDto;
import com.upi.upi_service.dto.PaymentRequestDto;
import com.upi.upi_service.dto.PaymentResponseDto;
import com.upi.upi_service.entity.VirtualPaymentAddress;
import com.upi.upi_service.entity.VpaStatus;
import com.upi.upi_service.exception.InsufficientBalanceException;
import com.upi.upi_service.exception.InvalidPinException;
import com.upi.upi_service.exception.VpaNotFoundException;
import com.upi.upi_service.repository.VpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final VpaRepository vpaRepository;
    private final AccountServiceClient accountServiceClient;
    private final KafkaTemplate<String, PaymentEventDto> kafkaTemplate;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.kafka.topic.payment-initiated}")
    private String paymentInitiatedTopic;

    public PaymentResponseDto initiatePayment(PaymentRequestDto request) {

        VirtualPaymentAddress senderVpa = vpaRepository.findByVpa(request.getSenderVpa())
                .orElseThrow(() ->
                        new VpaNotFoundException("Sender VPA not found: " + request.getSenderVpa()));

        VirtualPaymentAddress receiverVpa = vpaRepository.findByVpa(request.getReceiverVpa())
                .orElseThrow(() ->
                        new VpaNotFoundException("Receiver VPA not found: " + request.getReceiverVpa()));

        if (senderVpa.getStatus() != VpaStatus.ACTIVE) {
            throw new IllegalStateException("Sender VPA is not active");
        }

        if (receiverVpa.getStatus() != VpaStatus.ACTIVE) {
            throw new IllegalStateException("Receiver VPA is not active");
        }

        if (!passwordEncoder.matches(request.getPin(), senderVpa.getHashedPin())) {
            throw new InvalidPinException("Invalid UPI PIN");
        }

        Map<String, BigDecimal> balanceMap =
                accountServiceClient.getBalance(senderVpa.getAccountId());

        BigDecimal currentBalance = balanceMap.get("balance");

        if (currentBalance.compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Available: ₹" + currentBalance);
        }

        String transactionId = "TXN" + System.currentTimeMillis();

        try {

            log.info("Sender Account Id : {}", senderVpa.getAccountId());
            log.info("Receiver Account Id : {}", receiverVpa.getAccountId());

            // Debit sender
            accountServiceClient.updateBalance(
                    senderVpa.getAccountId(),
                    request.getAmount().negate());

            log.info("Debited ₹{} from {}",
                    request.getAmount(),
                    request.getSenderVpa());

            // Credit receiver
            accountServiceClient.updateBalance(
                    receiverVpa.getAccountId(),
                    request.getAmount());

            log.info("Credited ₹{} to {}",
                    request.getAmount(),
                    request.getReceiverVpa());

        } catch (Exception ex) {

            log.error("Payment failed", ex);

            throw new RuntimeException(
                    "Payment processing failed: " + ex.getMessage());
        }

        PaymentEventDto event = PaymentEventDto.builder()
                .transactionId(transactionId)
                .senderVpa(request.getSenderVpa())
                .receiverVpa(request.getReceiverVpa())
                .senderAccountId(senderVpa.getAccountId())
                .receiverAccountId(receiverVpa.getAccountId())
                .amount(request.getAmount())
                .remarks(request.getRemarks())
                .initiatedAt(LocalDateTime.now())
                .build();

        kafkaTemplate.send(
                paymentInitiatedTopic,
                transactionId,
                event
        );

        log.info("Published payment.initiated event for TXN: {}",
                transactionId);

        return PaymentResponseDto.builder()
                .transactionId(transactionId)
                .senderVpa(request.getSenderVpa())
                .receiverVpa(request.getReceiverVpa())
                .amount(request.getAmount())
                .status("SUCCESS")
                .remarks(request.getRemarks())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
