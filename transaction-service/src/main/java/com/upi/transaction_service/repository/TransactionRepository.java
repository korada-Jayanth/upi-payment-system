package com.upi.transaction_service.repository;

import com.upi.transaction_service.entity.Transaction;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionId(String transactionId);

    // Get all transactions where user is sender OR receiver
    @Query("SELECT t FROM Transaction t WHERE t.senderVpa = :vpa OR t.receiverVpa = :vpa ORDER BY t.initiatedAt DESC")
    List<Transaction> findByVpa(@Param("vpa") String vpa);

    List<Transaction> findBySenderAccountIdOrReceiverAccountId(
            Long senderAccountId, Long receiverAccountId);
}
