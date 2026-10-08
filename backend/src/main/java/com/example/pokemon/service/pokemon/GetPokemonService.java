package com.example.pokemon.service.pokemon;

import com.example.pokemon.exception.pokemon.PokemonNotFoundException;
import com.example.pokemon.integration.pokeapi.ExternalEvolutionChain;
import com.example.pokemon.integration.pokeapi.ExternalPokemon;
import com.example.pokemon.integration.pokeapi.ExternalSpecies;
import com.example.pokemon.integration.pokeapi.PokeApiClient;
import com.example.pokemon.repository.PokemonRepository;
import com.example.pokemon.model.pokemon.Pokemon;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetPokemonService {
    private final PokemonRepository repository;
    private final PokeApiClient api;

    public GetPokemonService(PokemonRepository repository, PokeApiClient api) {
        this.repository = repository;
        this.api = api;
    }

    public Pokemon execute(Long id) {
        return repository.findByPokeApiId(Math.toIntExact(id)).orElseGet(() -> livePokemon(id));
    }

    private Pokemon livePokemon(Long id) {
        ExternalPokemon external = api.fetchPokemon(id.intValue());
        if (external == null || external.id() == null || external.id() <= 0) {
            throw new PokemonNotFoundException(id);
        }
        ExternalSpecies species = api.fetchSpecies(id.intValue());
        ExternalEvolutionChain evolution = species.evolutionChainUrl() == null
                ? new ExternalEvolutionChain(List.of())
                : api.fetchEvolutionChain(species.evolutionChainUrl());
        return Pokemon.create(external.id(), external.name(), external.weight(), external.spriteUrl())
                .withId(external.id().longValue())
                .withDetails(external.types(), external.abilities(), external.stats(), species.description(), evolution.names());
    }
}
