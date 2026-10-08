package com.example.pokemon.service.pokemon;

import com.example.pokemon.exception.pokemon.PokemonNotFoundException;
import com.example.pokemon.repository.PokemonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeletePokemonService {
    private final PokemonRepository repository;

    public DeletePokemonService(PokemonRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void execute(Long id) {
        int pokeApiId = Math.toIntExact(id);
        repository.findByPokeApiId(pokeApiId)
                .orElseThrow(() -> new PokemonNotFoundException(id));
        repository.deleteByPokeApiId(pokeApiId);
    }
}
