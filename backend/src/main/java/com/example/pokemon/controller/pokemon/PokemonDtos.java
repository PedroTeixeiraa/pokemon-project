package com.example.pokemon.controller.pokemon;

import com.example.pokemon.model.pokemon.Pokemon;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;

public final class PokemonDtos {
    private PokemonDtos() {
    }

    public record Summary(Long id, Integer pokeApiId, String name, String spriteUrl,
                          List<String> types, int weight, List<String> abilities) {
        public static Summary from(Pokemon pokemon) {
            return new Summary(pokemon.id(), pokemon.pokeApiId(), pokemon.name(), pokemon.spriteUrl(),
                    pokemon.types(), pokemon.weight(), pokemon.abilities());
        }
    }

    public record Details(Long id, Integer pokeApiId, String name, String spriteUrl, int weight,
                          List<String> types, List<String> abilities, Map<String, Integer> stats,
                          String description, List<String> evolutionChain, String localizedName,
                          String region, List<String> internalTags, String notes) {
        public static Details from(Pokemon pokemon) {
            return new Details(pokemon.id(), pokemon.pokeApiId(), pokemon.name(), pokemon.spriteUrl(), pokemon.weight(),
                    pokemon.types(), pokemon.abilities(), pokemon.stats(), pokemon.description(), pokemon.evolutionChain(),
                    pokemon.localizedName(), pokemon.region(), pokemon.internalTags(), pokemon.notes());
        }
    }

    public record SyncRequest(@NotEmpty List<Integer> pokeApiIds) {
    }

    public record UpdateRequest(@Size(max = 100) String localizedName,
                                @Size(max = 100) String region,
                                @Size(max = 20) List<@Size(max = 40) String> internalTags,
                                @Size(max = 1000) String notes) {
    }

    public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {
        public static <T> PageResponse<T> from(Page<T> page) {
            return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(),
                    page.getTotalElements(), page.getTotalPages());
        }
    }
}
