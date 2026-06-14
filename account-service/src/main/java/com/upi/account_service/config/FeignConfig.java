package com.upi.account_service.config;

import com.upi.account_service.exception.UserNotFoundException;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            if (response.status() == 404) {
                return new UserNotFoundException("User not found via Feign call");
            }
            return new RuntimeException("Feign error: " + response.status());
        };
    }
}
