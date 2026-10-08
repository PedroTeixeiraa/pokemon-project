package com.example.pokemon.integration.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
public class RedisPokeApiCache implements PokeApiCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisPokeApiCache.class);
    private final StringRedisTemplate redis;

    public RedisPokeApiCache(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public Optional<String> get(String key) {
        try {
            return Optional.ofNullable(redis.opsForValue().get(key));
        } catch (RuntimeException exception) {
            LOGGER.warn("Redis read failed for key {}. Continuing without cache.", key, exception);
            return Optional.empty();
        }
    }

    @Override
    public void put(String key, String payload, Duration ttl) {
        try {
            redis.opsForValue().set(key, payload, ttl);
        } catch (RuntimeException exception) {
            LOGGER.warn("Redis write failed for key {}. Continuing without cache.", key, exception);
        }
    }
}
