package com.example.pokemon.controller.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
    private AuthDtos() {
    }

    public record Credentials(
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8) String password
    ) {
    }
}
