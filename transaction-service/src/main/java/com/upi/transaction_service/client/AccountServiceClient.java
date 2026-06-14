package com.upi.transaction_service.client;

import com.upi.transaction_service.dto.AccountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "account-service")
public interface AccountServiceClient {

    @GetMapping("/api/accounts/{accountId}")
    AccountDto getAccountById(@PathVariable("accountId") Long accountId);
}

