package com.upi.api_gateway.filter;

import com.upi.api_gateway.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public JwtAuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            String path = exchange.getRequest().getURI().getPath();
            log.info("Request incoming: {}", path);

            // 1. Get Authorization header
            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            // 2. Check header exists and starts with Bearer
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Missing or invalid Authorization header for path: {}", path);
                return unauthorizedResponse(exchange, "Missing or invalid Authorization header");
            }

            // 3. Extract and validate the token
            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {
                log.warn("Invalid JWT token for path: {}", path);
                return unauthorizedResponse(exchange, "Invalid or expired token");
            }

            // 4. Token is valid — extract user info and forward in headers
            String phoneNumber = jwtUtil.extractPhoneNumber(token);
            Long userId = jwtUtil.extractUserId(token);

            log.info("Authenticated user: {} (id: {})", phoneNumber, userId);

            // 5. Add user info as headers so downstream services know who is calling
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(r -> r
                            .header("X-User-Phone", phoneNumber)
                            .header("X-User-Id", String.valueOf(userId))
                    )
                    .build();

            return chain.filter(modifiedExchange);
        };
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        var buffer = exchange.getResponse().bufferFactory()
                .wrap(("{\"error\":\"" + message + "\"}").getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    // Config class required by AbstractGatewayFilterFactory
    public static class Config {
        // empty — no config needed for this filter
    }
}