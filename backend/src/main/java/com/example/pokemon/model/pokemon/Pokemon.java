package com.example.pokemon.model.pokemon;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record Pokemon(
        Long id,
        Integer pokeApiId,
        String name,
        String spriteUrl,
        int weight,
        List<String> types,
        List<String> abilities,
        Map<String, Integer> stats,
        String description,
        List<String> evolutionChain,
        String localizedName,
        String region,
        List<String> internalTags,
        String notes,
        Instant updatedAt
) {
    public Pokemon {
        types = immutableList(types);
        abilities = immutableList(abilities);
        stats = immutableMap(stats);
        evolutionChain = immutableList(evolutionChain);
        internalTags = immutableList(internalTags);
    }

    public static Pokemon create(Integer pokeApiId, String name, int weight, String spriteUrl) {
        if (pokeApiId == null || pokeApiId <= 0) {
            throw new IllegalArgumentException("PokeAPI id must be positive");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Pokemon name must not be blank");
        }
        if (weight <= 0) {
            throw new IllegalArgumentException("Pokemon weight must be positive");
        }
        return new Pokemon(null, pokeApiId, name.trim(), spriteUrl, weight, List.of(), List.of(),
                Map.of(), null, List.of(), null, null, List.of(), null, Instant.now());
    }

    public Pokemon withId(Long newId) {
        return new Pokemon(newId, pokeApiId, name, spriteUrl, weight, types, abilities, stats,
                description, evolutionChain, localizedName, region, internalTags, notes, Instant.now());
    }

    public Pokemon withDetails(List<String> newTypes, List<String> newAbilities,
                               Map<String, Integer> newStats, String newDescription,
                               List<String> newEvolutionChain) {
        return new Pokemon(id, pokeApiId, name, spriteUrl, weight, newTypes, newAbilities, newStats,
                newDescription, newEvolutionChain, localizedName, region, internalTags, notes, Instant.now());
    }

    public Pokemon withExternalData(String newName, String newSpriteUrl, int newWeight,
                                    List<String> newTypes, List<String> newAbilities,
                                    Map<String, Integer> newStats, String newDescription,
                                    List<String> newEvolutionChain) {
        return new Pokemon(id, pokeApiId, newName, newSpriteUrl, newWeight, newTypes, newAbilities,
                newStats, newDescription, newEvolutionChain, localizedName, region, internalTags, notes, Instant.now());
    }

    public Pokemon withLocalFields(String newLocalizedName, String newRegion,
                                   List<String> newInternalTags, String newNotes) {
        return new Pokemon(id, pokeApiId, name, spriteUrl, weight, types, abilities, stats,
                description, evolutionChain, newLocalizedName, newRegion, newInternalTags, newNotes, Instant.now());
    }

    private static <T> List<T> immutableList(List<T> values) {
        return values == null ? List.of() : Collections.unmodifiableList(new ArrayList<>(values));
    }

    private static <K, V> Map<K, V> immutableMap(Map<K, V> values) {
        return values == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(values));
    }
}
