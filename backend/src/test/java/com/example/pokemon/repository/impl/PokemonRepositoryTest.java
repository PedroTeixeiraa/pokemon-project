package com.example.pokemon.repository.impl;

import com.example.pokemon.model.pokemon.Pokemon;
import com.example.pokemon.model.persistence.PokemonEntity;
import com.example.pokemon.repository.jpa.PokemonJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop", "spring.sql.init.mode=never"})
class PokemonRepositoryTest {
    @Autowired
    private PokemonJpaRepository repository;

    @Test
    void persistsAndFindsPokemonByExternalId() {
        PokemonEntity entity = PokemonEntity.fromDomain(Pokemon.create(25, "Pikachu", 60, "sprite.png"));
        repository.save(entity);

        assertThat(repository.findByPokeApiId(25)).isPresent();
    }

    @Test
    void supportsPagination() {
        repository.save(PokemonEntity.fromDomain(Pokemon.create(1, "Bulbasaur", 69, "bulba.png")));
        repository.save(PokemonEntity.fromDomain(Pokemon.create(4, "Charmander", 85, "char.png")));

        Page<PokemonEntity> page = repository.findAll(PageRequest.of(0, 1));

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).hasSize(1);
    }
}
