package com.example.pokemon.model.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class UserTest {
    @Test
    void normalizesEmail() {
        User user = User.create("  USER@EXAMPLE.COM ", "hashed-password");

        assertThat(user.email()).isEqualTo("user@example.com");
    }

    @Test
    void rejectsBlankPasswordHash() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> User.create("user@example.com", " "))
                .withMessage("Password hash must not be blank");
    }
}
