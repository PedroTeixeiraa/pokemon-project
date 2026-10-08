package com.example.pokemon.service.pokemon;

import com.example.pokemon.integration.pokeapi.PokeApiClient;
import com.example.pokemon.repository.PokemonRepository;
import com.example.pokemon.model.pokemon.Pokemon;
import com.example.pokemon.integration.pokeapi.ExternalEvolutionChain;
import com.example.pokemon.integration.pokeapi.ExternalPokemon;
import com.example.pokemon.integration.pokeapi.ExternalSpecies;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SyncPokemonService {
    private final PokeApiClient api;
    private final PokemonRepository repository;

    public SyncPokemonService(PokeApiClient api, PokemonRepository repository) {
        this.api = api;
        this.repository = repository;
    }

    public SyncResult execute(SyncPokemonCommand command) {
        if (command == null || command.pokeApiIds() == null || command.pokeApiIds().isEmpty()) {
            throw new IllegalArgumentException("At least one PokeAPI id is required");
        }
        int inserted = 0;
        int updated = 0;
        for (Integer id : command.pokeApiIds()) {
            Pokemon current = repository.findByPokeApiId(id).orElse(null);
            Pokemon synchronizedPokemon = synchronize(id, current);
            repository.save(synchronizedPokemon);
            if (current == null) {
                inserted++;
            } else {
                updated++;
            }
        }
        return new SyncResult(inserted, updated);
    }

    private Pokemon synchronize(Integer id, Pokemon current) {
        ExternalPokemon external = api.fetchPokemon(id);
        ExternalSpecies species = api.fetchSpecies(id);
        ExternalEvolutionChain evolution = species.evolutionChainUrl() == null
                ? new ExternalEvolutionChain(List.of())
                : api.fetchEvolutionChain(species.evolutionChainUrl());
        if (current == null) {
            return Pokemon.create(external.id(), external.name(), external.weight(), external.spriteUrl())
                    .withExternalData(external.name(), external.spriteUrl(), external.weight(), external.types(),
                            external.abilities(), external.stats(), species.description(), evolution.names());
        }
        return current.withExternalData(external.name(), external.spriteUrl(), external.weight(), external.types(),
                external.abilities(), external.stats(), species.description(), evolution.names());
    }
}
