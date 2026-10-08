package com.example.pokemon.service.pokemon;

import java.util.List;

public record SyncPokemonCommand(List<Integer> pokeApiIds) {
}
