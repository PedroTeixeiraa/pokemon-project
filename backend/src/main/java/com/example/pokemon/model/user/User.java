package com.example.pokemon.model.user;

import java.time.Instant;
import java.util.Locale;

public record User(Long id, String email, String passwordHash, Instant createdAt) {
    public static User create(String email, String passwordHash) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash must not be blank");
        }
        return new User(null, email.trim().toLowerCase(Locale.ROOT), passwordHash, Instant.now());
    }
}
