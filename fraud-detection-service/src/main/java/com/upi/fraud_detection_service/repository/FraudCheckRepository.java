package com.upi.fraud_detection_service.repository;

import com.upi.fraud_detection_service.document.FraudCheck;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudCheckRepository extends MongoRepository<FraudCheck, String> {

    List<FraudCheck> findBySenderVpa(String senderVpa);

    List<FraudCheck> findByFlaggedTrue();
}