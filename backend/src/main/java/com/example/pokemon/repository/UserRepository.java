package com.example.pokemon.repository;

import com.example.pokemon.model.user.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);

    User save(User user);
}
