package com.example.pokemon.integration.pokeapi;

import com.example.pokemon.exception.integration.PokeApiException;
import com.example.pokemon.integration.cache.PokeApiCache;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

class PokeApiHttpClientTest {
    private final RestClient.Builder builder = RestClient.builder();
    private final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
    private final PokeApiHttpClient client = new PokeApiHttpClient(builder, "http://poke.test/api/v2");

    @AfterEach
    void verifyRequests() {
        server.verify();
    }

    @Test
    void mapsPokemonResponse() {
        server.expect(requestTo("http://poke.test/api/v2/pokemon/25"))
                .andRespond(withSuccess("""
                        {"id":25,"name":"pikachu","weight":60,"sprites":{"front_default":"sprite.png"},
                         "types":[{"type":{"name":"electric"}}],
                         "abilities":[{"ability":{"name":"static"}}],
                         "stats":[{"base_stat":35,"stat":{"name":"hp"}}]}
                        """, org.springframework.http.MediaType.APPLICATION_JSON));

        ExternalPokemon pokemon = client.fetchPokemon(25);

        assertThat(pokemon.id()).isEqualTo(25);
        assertThat(pokemon.name()).isEqualTo("pikachu");
        assertThat(pokemon.types()).containsExactly("electric");
        assertThat(pokemon.abilities()).containsExactly("static");
        assertThat(pokemon.stats()).containsEntry("hp", 35);
    }

    @Test
    void mapsMissingSpriteAsNull() {
        server.expect(requestTo("http://poke.test/api/v2/pokemon/25"))
                .andRespond(withSuccess("""
                        {"id":25,"name":"pikachu","weight":60,"sprites":{}}
                        """,
                        org.springframework.http.MediaType.APPLICATION_JSON));

        assertThat(client.fetchPokemon(25).spriteUrl()).isNull();
    }

    @Test
    void convertsUpstreamServerErrorToPokeApiException() {
        server.expect(requestTo("http://poke.test/api/v2/pokemon/25")).andRespond(withServerError());

        assertThatThrownBy(() -> client.fetchPokemon(25))
                .isInstanceOf(PokeApiException.class)
                .hasMessageContaining("PokeAPI request failed");
    }

    @Test
    void mapsPokemonPageAndExtractsIds() {
        server.expect(requestTo("http://poke.test/api/v2/pokemon?offset=20&limit=20"))
                .andRespond(withSuccess("""
                        {"count":1302,"next":"next","previous":"previous",
                         "results":[{"name":"pikachu","url":"https://pokeapi.co/api/v2/pokemon/25/"}]}
                        """, org.springframework.http.MediaType.APPLICATION_JSON));

        ExternalPokemonPage page = client.fetchPokemonPage(20, 20);

        assertThat(page.count()).isEqualTo(1302);
        assertThat(page.offset()).isEqualTo(20);
        assertThat(page.limit()).isEqualTo(20);
        assertThat(page.results()).singleElement().extracting(ExternalPokemonSummary::id).isEqualTo(25);
    }

    @Test
    void returnsCachedResponseWithoutCallingUpstream() {
        MapCache cache = new MapCache();
        cache.values.put("/pokemon/25", """
                {"id":25,"name":"pikachu","weight":60,"sprites":{"front_default":"cached.png"}}
                """);
        PokeApiHttpClient cachedClient = new PokeApiHttpClient(RestClient.builder(), "http://poke.test/api/v2", cache);

        assertThat(cachedClient.fetchPokemon(25).spriteUrl()).isEqualTo("cached.png");
    }

    private static final class MapCache implements PokeApiCache {
        private final Map<String, String> values = new HashMap<>();

        @Override
        public java.util.Optional<String> get(String key) {
            return java.util.Optional.ofNullable(values.get(key));
        }

        @Override
        public void put(String key, String payload, Duration ttl) {
            values.put(key, payload);
        }
    }
}
