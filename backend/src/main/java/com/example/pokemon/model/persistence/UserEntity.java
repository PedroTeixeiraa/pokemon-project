package com.example.pokemon.model.persistence;

import com.example.pokemon.model.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private Instant createdAt;

    protected UserEntity() {
    }

    public static UserEntity fromDomain(User user) {
        UserEntity entity = new UserEntity();
        entity.id = user.id();
        entity.email = user.email();
        entity.passwordHash = user.passwordHash();
        entity.createdAt = user.createdAt();
        return entity;
    }

    public User toDomain() {
        return new User(id, email, passwordHash, createdAt);
    }
}
