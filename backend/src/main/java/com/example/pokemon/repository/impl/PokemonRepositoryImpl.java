package com.example.pokemon.repository.impl;

import com.example.pokemon.repository.PokemonRepository;
import com.example.pokemon.model.pokemon.Pokemon;
import com.example.pokemon.model.persistence.PokemonEntity;
import com.example.pokemon.repository.jpa.PokemonJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PokemonRepositoryImpl implements PokemonRepository {
    private final PokemonJpaRepository repository;

    public PokemonRepositoryImpl(PokemonJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Pokemon> findById(Long id) {
        return repository.findById(id).map(PokemonEntity::toDomain);
    }

    @Override
    public Optional<Pokemon> findByPokeApiId(Integer pokeApiId) {
        return repository.findByPokeApiId(pokeApiId).map(PokemonEntity::toDomain);
    }

    @Override
    public Page<Pokemon> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(PokemonEntity::toDomain);
    }

    @Override
    public Pokemon save(Pokemon pokemon) {
        return repository.save(PokemonEntity.fromDomain(pokemon)).toDomain();
    }

    @Override
    public void deleteByPokeApiId(Integer pokeApiId) {
        repository.deleteByPokeApiId(pokeApiId);
    }
}
