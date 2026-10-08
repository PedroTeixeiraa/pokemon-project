package com.example.pokemon.repository.jpa;

import com.example.pokemon.model.persistence.PokemonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PokemonJpaRepository extends JpaRepository<PokemonEntity, Long> {
    Optional<PokemonEntity> findByPokeApiId(Integer pokeApiId);

    void deleteByPokeApiId(Integer pokeApiId);
}
