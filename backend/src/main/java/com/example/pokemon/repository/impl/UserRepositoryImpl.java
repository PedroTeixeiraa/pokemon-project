package com.example.pokemon.repository.impl;

import com.example.pokemon.repository.UserRepository;
import com.example.pokemon.model.user.User;
import com.example.pokemon.model.persistence.UserEntity;
import com.example.pokemon.repository.jpa.UserJpaRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository repository;

    public UserRepositoryImpl(UserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email.toLowerCase()).map(UserEntity::toDomain);
    }

    @Override
    public User save(User user) {
        try {
            return repository.save(UserEntity.fromDomain(user)).toDomain();
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalArgumentException("Email is already registered", exception);
        }
    }
}
