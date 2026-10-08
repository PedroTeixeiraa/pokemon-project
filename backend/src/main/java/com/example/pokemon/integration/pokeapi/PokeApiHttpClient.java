package com.example.pokemon.integration.pokeapi;

import com.example.pokemon.exception.integration.PokeApiException;
import com.example.pokemon.integration.cache.NoOpPokeApiCache;
import com.example.pokemon.integration.cache.PokeApiCache;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.pokemon.integration.pokeapi.PokeApiClient;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.time.Duration;

@Component
public class PokeApiHttpClient implements PokeApiClient {
    private final RestClient client;
    private final ObjectMapper objectMapper;
    private final PokeApiCache cache;
    private final Duration cacheTtl;

    @Autowired
    public PokeApiHttpClient(RestClient.Builder builder,
                             org.springframework.core.env.Environment environment,
                             PokeApiCache cache) {
        this(builder,
                environment.getProperty("pokeapi.base-url", "https://pokeapi.co/api/v2"),
                environment.getProperty("pokeapi.connect-timeout-ms", Integer.class, 3000),
                environment.getProperty("pokeapi.read-timeout-ms", Integer.class, 5000),
                cache,
                Duration.ofSeconds(environment.getProperty("app.cache.pokeapi-ttl-seconds", Long.class, 600L)));
    }

    public PokeApiHttpClient(RestClient.Builder builder, String baseUrl) {
        this(builder, baseUrl, new NoOpPokeApiCache());
    }

    public PokeApiHttpClient(RestClient.Builder builder, String baseUrl, PokeApiCache cache) {
        this.client = builder.baseUrl(baseUrl).build();
        this.objectMapper = new ObjectMapper();
        this.cache = cache;
        this.cacheTtl = Duration.ofMinutes(10);
    }

    private PokeApiHttpClient(RestClient.Builder builder, String baseUrl, int connectTimeoutMs,
                              int readTimeoutMs, PokeApiCache cache, Duration cacheTtl) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeoutMs);
        requestFactory.setReadTimeout(readTimeoutMs);
        this.client = builder.requestFactory(requestFactory).baseUrl(baseUrl).build();
        this.objectMapper = new ObjectMapper();
        this.cache = cache;
        this.cacheTtl = cacheTtl;
    }

    @Override
    public ExternalPokemonPage fetchPokemonPage(int offset, int limit) {
        JsonNode root = getJson("/pokemon?offset=" + offset + "&limit=" + limit);
        List<ExternalPokemonSummary> results = new ArrayList<>();
        root.path("results").forEach(item -> {
            String url = item.path("url").asText();
            results.add(new ExternalPokemonSummary(extractId(url), item.path("name").asText(), url));
        });
        return new ExternalPokemonPage(root.path("count").asInt(), results, offset, limit);
    }

    @Override
    public ExternalPokemon fetchPokemon(int pokeApiId) {
        JsonNode root = getJson("/pokemon/" + pokeApiId);
        List<String> types = new ArrayList<>();
        root.path("types").forEach(item -> types.add(item.path("type").path("name").asText()));
        List<String> abilities = new ArrayList<>();
        root.path("abilities").forEach(item -> abilities.add(item.path("ability").path("name").asText()));
        Map<String, Integer> stats = new LinkedHashMap<>();
        root.path("stats").forEach(item -> stats.put(item.path("stat").path("name").asText(), item.path("base_stat").asInt()));
        String sprite = root.path("sprites").path("front_default").isMissingNode()
                || root.path("sprites").path("front_default").isNull()
                ? null : root.path("sprites").path("front_default").asText();
        return new ExternalPokemon(root.path("id").asInt(), root.path("name").asText(),
                root.path("weight").asInt(), sprite, types, abilities, stats);
    }

    @Override
    public ExternalSpecies fetchSpecies(int pokeApiId) {
        JsonNode root = getJson("/pokemon-species/" + pokeApiId);
        String description = root.path("flavor_text_entries").findValues("flavor_text").stream()
                .findFirst().map(JsonNode::asText).orElse(null);
        String evolutionUrl = root.path("evolution_chain").path("url").asText(null);
        return new ExternalSpecies(description, evolutionUrl);
    }

    @Override
    public ExternalEvolutionChain fetchEvolutionChain(String url) {
        JsonNode root = getJsonAbsolute(url);
        List<String> names = new ArrayList<>();
        collectEvolutionNames(root.path("chain"), names);
        return new ExternalEvolutionChain(names);
    }

    private void collectEvolutionNames(JsonNode node, List<String> names) {
        if (node.isMissingNode() || node.isNull()) {
            return;
        }
        String name = node.path("species").path("name").asText(null);
        if (name != null) {
            names.add(name);
        }
        node.path("evolves_to").forEach(child -> collectEvolutionNames(child, names));
    }

    private JsonNode getJson(String path) {
        return getJsonAbsolute(path);
    }

    private JsonNode getJsonAbsolute(String path) {
        try {
            String cached = cache.get(path).orElse(null);
            if (cached != null) {
                return objectMapper.readTree(cached);
            }
            String body = client.get().uri(path).accept(MediaType.APPLICATION_JSON).retrieve().body(String.class);
            cache.put(path, body, cacheTtl);
            return objectMapper.readTree(body);
        } catch (HttpClientErrorException.NotFound exception) {
            throw new PokeApiException("PokeAPI resource not found: " + path, exception, true);
        } catch (RestClientException | java.io.IOException exception) {
            throw new PokeApiException("PokeAPI request failed: " + path, exception);
        }
    }

    private int extractId(String url) {
        String normalized = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
        return Integer.parseInt(normalized.substring(normalized.lastIndexOf('/') + 1));
    }
}
