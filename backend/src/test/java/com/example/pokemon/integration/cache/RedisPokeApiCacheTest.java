package com.example.pokemon.integration.cache;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RedisPokeApiCacheTest {
    private final StringRedisTemplate redis = mock(StringRedisTemplate.class);
    private final ValueOperations<String, String> values = mock(ValueOperations.class);
    private final RedisPokeApiCache cache = new RedisPokeApiCache(redis);

    @Test
    void readsCachedValue() {
        when(redis.opsForValue()).thenReturn(values);
        when(values.get("key")).thenReturn("payload");

        assertThat(cache.get("key")).contains("payload");
    }

    @Test
    void treatsRedisReadFailureAsCacheMiss() {
        when(redis.opsForValue()).thenThrow(new IllegalStateException("Redis unavailable"));

        assertThat(cache.get("key")).isEmpty();
    }

    @Test
    void ignoresRedisWriteFailure() {
        when(redis.opsForValue()).thenReturn(values);
        doThrow(new IllegalStateException("Redis unavailable"))
                .when(values).set("key", "payload", Duration.ofMinutes(10));

        cache.put("key", "payload", Duration.ofMinutes(10));
    }
}
