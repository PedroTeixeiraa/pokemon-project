package com.example.pokemon.integration.pokeapi;

import java.util.List;

public record ExternalPokemonPage(int count, List<ExternalPokemonSummary> results, int offset, int limit) {
}
