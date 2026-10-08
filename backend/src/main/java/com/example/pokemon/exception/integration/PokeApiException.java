package com.example.pokemon.exception.integration;

public class PokeApiException extends RuntimeException {
    private final boolean notFound;

    public PokeApiException(String message, Throwable cause) {
        this(message, cause, false);
    }

    public PokeApiException(String message, Throwable cause, boolean notFound) {
        super(message, cause);
        this.notFound = notFound;
    }

    public boolean notFound() {
        return notFound;
    }
}
