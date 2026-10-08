package com.example.pokemon.exception.auth;

public class EmailAlreadyRegisteredException extends RuntimeException {
    public EmailAlreadyRegisteredException(String email) {
        super("Email is already registered: " + email);
    }
}
