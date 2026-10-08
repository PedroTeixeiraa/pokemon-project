package com.example.pokemon.service.auth;

public record AuthUser(Long id, String email, String passwordHash) {
}
