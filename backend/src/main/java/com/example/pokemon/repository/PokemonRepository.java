package com.example.pokemon.repository;

import com.example.pokemon.model.pokemon.Pokemon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PokemonRepository {
    Optional<Pokemon> findById(Long id);

    Optional<Pokemon> findByPokeApiId(Integer pokeApiId);

    Page<Pokemon> findAll(Pageable pageable);

    Pokemon save(Pokemon pokemon);

    void deleteByPokeApiId(Integer pokeApiId);
}
