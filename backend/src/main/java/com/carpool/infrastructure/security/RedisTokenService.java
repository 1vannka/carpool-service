package com.carpool.infrastructure.security;

import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.ScanOptions;
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
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions()
                    .match("refresh:" + userId + ":*")
                    .count(100).build());
            while (cursor.hasNext()) {
                connection.del(cursor.next());
            }
            return null;
        });
    }
}