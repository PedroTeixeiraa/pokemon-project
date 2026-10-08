package com.example.pokemon.exception.pokemon;

public class PokemonNotFoundException extends RuntimeException {
    public PokemonNotFoundException(Long id) {
        super("Pokemon not found: " + id);
    }
}
