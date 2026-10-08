package com.example.pokemon.integration.cache;

import java.time.Duration;
import java.util.Optional;

public final class NoOpPokeApiCache implements PokeApiCache {
    @Override
    public Optional<String> get(String key) {
        return Optional.empty();
    }

    @Override
    public void put(String key, String payload, Duration ttl) {
        // Deliberately disabled for unit tests that mock the upstream HTTP server.
    }
}
