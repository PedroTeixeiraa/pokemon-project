package com.example.pokemon.service.pokemon;

import com.example.pokemon.integration.pokeapi.ExternalPokemon;
import com.example.pokemon.integration.pokeapi.ExternalPokemonPage;
import com.example.pokemon.integration.pokeapi.PokeApiClient;
import com.example.pokemon.model.pokemon.Pokemon;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListPokemonService {
    private final PokeApiClient api;

    public ListPokemonService(PokeApiClient api) {
        this.api = api;
    }

    public Page<Pokemon> execute(Pageable pageable) {
        int offset = pageable.getPageNumber() * pageable.getPageSize();
        ExternalPokemonPage page = api.fetchPokemonPage(offset, pageable.getPageSize());
        List<Pokemon> content = page.results().stream().map(summary -> {
            ExternalPokemon external = api.fetchPokemon(summary.id());
            return Pokemon.create(external.id(), external.name(), external.weight(), external.spriteUrl())
                    .withId(external.id().longValue())
                    .withDetails(external.types(), external.abilities(), external.stats(), null, List.of());
        }).toList();
        return new PageImpl<>(content, pageable, page.count());
    }
}
