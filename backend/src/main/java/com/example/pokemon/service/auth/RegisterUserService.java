package com.example.pokemon.service.auth;

import com.example.pokemon.exception.auth.EmailAlreadyRegisteredException;
import com.example.pokemon.repository.UserRepository;
import com.example.pokemon.model.user.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterUserService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserService(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthUser execute(RegisterUserCommand command) {
        validate(command);
        String normalizedEmail = command.email().trim().toLowerCase();
        if (users.findByEmail(normalizedEmail).isPresent()) {
            throw new EmailAlreadyRegisteredException(normalizedEmail);
        }
        User saved = users.save(User.create(normalizedEmail, passwordEncoder.encode(command.password())));
        return new AuthUser(saved.id(), saved.email(), null);
    }

    private void validate(RegisterUserCommand command) {
        if (command == null || command.email() == null || command.email().isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        if (command.password() == null || command.password().length() < 8) {
            throw new IllegalArgumentException("Password must contain at least 8 characters");
        }
    }
}
