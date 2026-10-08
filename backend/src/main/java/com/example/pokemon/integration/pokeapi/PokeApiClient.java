package com.example.pokemon.integration.pokeapi;

import com.example.pokemon.integration.pokeapi.ExternalEvolutionChain;
import com.example.pokemon.integration.pokeapi.ExternalPokemon;
import com.example.pokemon.integration.pokeapi.ExternalSpecies;

public interface PokeApiClient {
    ExternalPokemonPage fetchPokemonPage(int offset, int limit);

    ExternalPokemon fetchPokemon(int pokeApiId);

    ExternalSpecies fetchSpecies(int pokeApiId);

    ExternalEvolutionChain fetchEvolutionChain(String url);
}
