package com.upi.user_service.service;

import com.upi.user_service.dto.*;
import com.upi.user_service.entity.User;
import com.upi.user_service.repository.UserRepository;
import com.upi.user_service.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    public AuthResponse register(RegisterRequest request) {

        // 1. Validate no duplicates
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // 2. Build and save user
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .upiPin(passwordEncoder.encode(request.getUpiPin()))
                .status(User.UserStatus.ACTIVE)
                .build();

        user = userRepository.save(user);
        log.info("New user registered: {}", user.getPhoneNumber());

        // 3. Generate JWT
        String token = jwtUtil.generateToken(user.getPhoneNumber(), user.getId());

        // 4. Cache token in Redis (expires same time as JWT)
        redisTemplate.opsForValue().set(
                "token:" + user.getPhoneNumber(),
                token,
                24, TimeUnit.HOURS
        );

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .message("Registration successful")
                .build();
    }

    public AuthResponse login(LoginRequest request) {

        // 1. Find user
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // 2. Check account status
        if (user.getStatus() == User.UserStatus.BLOCKED) {
            throw new RuntimeException("Account is blocked. Contact support.");
        }

        // 3. Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // 4. Generate JWT and cache it
        String token = jwtUtil.generateToken(user.getPhoneNumber(), user.getId());

        redisTemplate.opsForValue().set(
                "token:" + user.getPhoneNumber(),
                token,
                24, TimeUnit.HOURS
        );

        log.info("User logged in: {}", user.getPhoneNumber());

        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .message("Login successful")
                .build();
    }

    public void logout(String phoneNumber) {
        redisTemplate.delete("token:" + phoneNumber);
        log.info("User logged out: {}", phoneNumber);
    }
}

