package com.upi.upi_service.repository;

import com.upi.upi_service.entity.VirtualPaymentAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VpaRepository extends JpaRepository<VirtualPaymentAddress, Long> {

    Optional<VirtualPaymentAddress> findByVpa(String vpa);

    Optional<VirtualPaymentAddress> findByUserId(Long userId);

    boolean existsByVpa(String vpa);
}


