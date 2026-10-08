package com.example.pokemon.service.auth;

import com.example.pokemon.exception.auth.InvalidCredentialsException;
import com.example.pokemon.repository.UserRepository;
import com.example.pokemon.model.user.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final TokenIssuer tokenIssuer;

    public LoginService(UserRepository users, PasswordEncoder passwordEncoder, TokenIssuer tokenIssuer) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.tokenIssuer = tokenIssuer;
    }

    public LoginResult execute(LoginCommand command) {
        if (command == null || command.email() == null || command.password() == null) {
            throw new InvalidCredentialsException();
        }
        User user = users.findByEmail(command.email().trim().toLowerCase())
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(command.password(), user.passwordHash())) {
            throw new InvalidCredentialsException();
        }
        return new LoginResult(tokenIssuer.issue(user), new AuthUser(user.id(), user.email(), null));
    }
}
