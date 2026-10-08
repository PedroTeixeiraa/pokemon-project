package com.example.pokemon.service.auth;

import com.example.pokemon.exception.auth.EmailAlreadyRegisteredException;
import com.example.pokemon.exception.auth.InvalidCredentialsException;
import com.example.pokemon.repository.UserRepository;
import com.example.pokemon.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthUseCaseTest {
    private final UserRepository users = mock(UserRepository.class);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Test
    void registrationStoresHashedPasswordAndReturnsSafeUser() {
        when(users.findByEmail("user@example.com")).thenReturn(Optional.empty());
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        RegisterUserService useCase = new RegisterUserService(users, encoder);

        AuthUser result = useCase.execute(new RegisterUserCommand("USER@EXAMPLE.COM", "secret123"));

        assertThat(result.email()).isEqualTo("user@example.com");
        assertThat(result.passwordHash()).isNull();
    }

    @Test
    void registrationRejectsExistingEmail() {
        when(users.findByEmail("user@example.com"))
                .thenReturn(Optional.of(User.create("user@example.com", "hash")));
        RegisterUserService useCase = new RegisterUserService(users, encoder);

        assertThatThrownBy(() -> useCase.execute(new RegisterUserCommand("user@example.com", "secret123")))
                .isInstanceOf(EmailAlreadyRegisteredException.class);
    }

    @Test
    void loginRejectsInvalidPassword() {
        when(users.findByEmail("user@example.com"))
                .thenReturn(Optional.of(User.create("user@example.com", encoder.encode("correct"))));
        LoginService useCase = new LoginService(users, encoder, email -> "token");

        assertThatThrownBy(() -> useCase.execute(new LoginCommand("user@example.com", "wrong")))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
