package com.example.pokemon.integration.pokeapi;

import java.util.List;
import java.util.Map;

public record ExternalPokemon(Integer id, String name, int weight, String spriteUrl,
                              List<String> types, List<String> abilities, Map<String, Integer> stats) {
}
