package com.example.pokemon.service.auth;

import com.example.pokemon.model.user.User;

@FunctionalInterface
public interface TokenIssuer {
    String issue(User user);
}
