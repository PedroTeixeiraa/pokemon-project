package com.example.pokemon.exception.pokemon;

public class PokemonNotSyncedException extends RuntimeException {
    public PokemonNotSyncedException(Long id) {
        super("Pokemon must be synchronized before local fields can be edited: " + id);
    }
}
