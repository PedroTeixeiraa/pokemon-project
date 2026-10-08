package com.example.pokemon.integration.cache;

import java.time.Duration;
import java.util.Optional;

public interface PokeApiCache {
    Optional<String> get(String key);

    void put(String key, String payload, Duration ttl);
}
