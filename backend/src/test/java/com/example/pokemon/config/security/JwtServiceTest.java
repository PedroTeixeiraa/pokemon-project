package com.example.pokemon.config.security;

import com.example.pokemon.model.user.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {
    @Test
    void createsTokenWithUserClaims() {
        JwtService service = new JwtService("a-very-long-development-secret-key-for-tests", 60);

        String token = service.createToken("42", "user@example.com");

        assertThat(service.userId(token)).isEqualTo("42");
        assertThat(service.email(token)).isEqualTo("user@example.com");
    }

    @Test
    void issuesTokenForDomainUser() {
        JwtService service = new JwtService("a-very-long-development-secret-key-for-tests", 60);
        User user = User.create("user@example.com", "hash");

        assertThat(service.issue(user)).isNotBlank();
    }
}
