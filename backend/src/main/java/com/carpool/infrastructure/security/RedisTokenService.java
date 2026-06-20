package com.carpool.infrastructure.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Service
public class RedisTokenService {

    private final StringRedisTemplate redisTemplate;

    public RedisTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveRefreshToken(Long userId, String token, long durationMs) {
        String key = "refresh:" + userId + ":" + token;
        redisTemplate.opsForValue().set(key, "active", Duration.ofMillis(durationMs));
    }

    public boolean isRefreshTokenValid(Long userId, String token) {
        String key = "refresh:" + userId + ":" + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void deleteRefreshToken(Long userId, String token) {
        String key = "refresh:" + userId + ":" + token;
        redisTemplate.delete(key);
    }

    public void deleteAllUserTokens(Long userId) {
        Set<String> keys = redisTemplate.keys("refresh:" + userId + ":*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}