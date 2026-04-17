package com.example.usermanagement.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TokenBlacklistService {

    private final RedisTemplate<String, String> redisTemplate;

    public TokenBlacklistService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklistToken(String token, long expirationTimeInSeconds) {
        redisTemplate.opsForValue().set(token, "blacklisted", Duration.ofSeconds(expirationTimeInSeconds));
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }
}
