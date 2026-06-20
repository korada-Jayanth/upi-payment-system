package com.upi.upi_service.client;

import com.upi.upi_service.config.FeignConfig;
import com.upi.upi_service.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        configuration = FeignConfig.class
)
public interface UserServiceClient {

    @GetMapping("/api/auth/users/{userId}")
    UserDto getUserById(@PathVariable("userId") Long userId);
}