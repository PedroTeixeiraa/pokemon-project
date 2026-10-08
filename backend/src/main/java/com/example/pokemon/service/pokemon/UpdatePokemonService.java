package com.example.pokemon.service.pokemon;

import com.example.pokemon.exception.integration.PokeApiException;
import com.example.pokemon.exception.pokemon.PokemonNotFoundException;
import com.example.pokemon.exception.pokemon.PokemonNotSyncedException;
import com.example.pokemon.integration.pokeapi.PokeApiClient;
import com.example.pokemon.repository.PokemonRepository;
import com.example.pokemon.model.pokemon.Pokemon;
import org.springframework.stereotype.Service;

@Service
public class UpdatePokemonService {
    private final PokemonRepository repository;
    private final PokeApiClient api;

    public UpdatePokemonService(PokemonRepository repository, PokeApiClient api) {
        this.repository = repository;
        this.api = api;
    }

    public Pokemon execute(Long id, UpdatePokemonCommand command) {
        int pokeApiId = Math.toIntExact(id);
        Pokemon current = repository.findByPokeApiId(pokeApiId).orElseGet(() -> {
            assertPokemonExistsInPokeApi(id, pokeApiId);
            throw new PokemonNotSyncedException(id);
        });
        return repository.save(current.withLocalFields(command.localizedName(), command.region(),
                command.internalTags(), command.notes()));
    }

    private void assertPokemonExistsInPokeApi(Long id, int pokeApiId) {
        try {
            if (api.fetchPokemon(pokeApiId) == null) {
                throw new PokemonNotFoundException(id);
            }
        } catch (PokeApiException exception) {
            if (exception.notFound()) {
                throw new PokemonNotFoundException(id);
            }
            throw exception;
        }
    }
}
