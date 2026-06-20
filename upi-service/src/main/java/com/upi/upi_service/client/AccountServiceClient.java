package com.upi.upi_service.client;

import com.upi.upi_service.config.FeignOkHttpConfig;
import com.upi.upi_service.dto.AccountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(name = "account-service")
public interface AccountServiceClient {

    @GetMapping("/api/accounts/{accountId}")
    AccountDto getAccountById(@PathVariable("accountId") Long accountId);

    @GetMapping("/api/accounts/{accountId}/balance")
    Map<String, BigDecimal> getBalance(@PathVariable("accountId") Long accountId);

    @PatchMapping("/api/accounts/{accountId}/balance")
    AccountDto updateBalance(@PathVariable("accountId") Long accountId,
                             @RequestParam("amount") BigDecimal amount);
}

