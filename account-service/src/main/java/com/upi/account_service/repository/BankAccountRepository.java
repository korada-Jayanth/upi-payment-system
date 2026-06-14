package com.upi.account_service.repository;

import com.upi.account_service.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    List<BankAccount> findByUserId(Long userId);

    Optional<BankAccount> findByAccountNumber(String accountNumber);

    Optional<BankAccount> findByUpiId(String upiId);

    boolean existsByAccountNumber(String accountNumber);
}
