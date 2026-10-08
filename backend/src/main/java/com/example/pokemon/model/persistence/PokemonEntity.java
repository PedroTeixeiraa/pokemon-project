package com.example.pokemon.model.persistence;

import com.example.pokemon.model.pokemon.Pokemon;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "pokemon")
public class PokemonEntity {
    private static final ObjectMapper JSON = new ObjectMapper();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "poke_api_id", nullable = false, unique = true)
    private Integer pokeApiId;

    @Column(nullable = false)
    private String name;

    private String spriteUrl;

    @Column(nullable = false)
    private int weight;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String typesJson;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String abilitiesJson;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String statsJson;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String evolutionChainJson;

    private String localizedName;
    private String region;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String internalTagsJson;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private Instant updatedAt;

    protected PokemonEntity() {
    }

    public static PokemonEntity fromDomain(Pokemon pokemon) {
        PokemonEntity entity = new PokemonEntity();
        entity.id = pokemon.id();
        entity.pokeApiId = pokemon.pokeApiId();
        entity.name = pokemon.name();
        entity.spriteUrl = pokemon.spriteUrl();
        entity.weight = pokemon.weight();
        entity.typesJson = write(pokemon.types());
        entity.abilitiesJson = write(pokemon.abilities());
        entity.statsJson = write(pokemon.stats());
        entity.description = pokemon.description();
        entity.evolutionChainJson = write(pokemon.evolutionChain());
        entity.localizedName = pokemon.localizedName();
        entity.region = pokemon.region();
        entity.internalTagsJson = write(pokemon.internalTags());
        entity.notes = pokemon.notes();
        entity.updatedAt = pokemon.updatedAt();
        return entity;
    }

    public Pokemon toDomain() {
        return new Pokemon(id, pokeApiId, name, spriteUrl, weight,
                readList(typesJson), readList(abilitiesJson), readMap(statsJson), description,
                readList(evolutionChainJson), localizedName, region, readList(internalTagsJson), notes, updatedAt);
    }

    private static String write(Object value) {
        try {
            return JSON.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize Pokemon field", exception);
        }
    }

    private static List<String> readList(String value) {
        try {
            return JSON.readValue(value == null ? "[]" : value, new TypeReference<>() {});
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not deserialize Pokemon list", exception);
        }
    }

    private static Map<String, Integer> readMap(String value) {
        try {
            return JSON.readValue(value == null ? "{}" : value, new TypeReference<>() {});
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not deserialize Pokemon map", exception);
        }
    }
}
