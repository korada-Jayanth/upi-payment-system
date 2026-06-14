package com.upi.upi_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "virtual_payment_addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VirtualPaymentAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String vpa;              // e.g. jayanth@upi

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private String hashedPin;        // BCrypt hashed 4-digit UPI PIN

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VpaStatus status;        // ACTIVE, BLOCKED

    @CreationTimestamp
    private LocalDateTime createdAt;
}
