package com.example.pokemon.service.pokemon;

import com.example.pokemon.exception.pokemon.PokemonNotFoundException;
import com.example.pokemon.integration.pokeapi.ExternalPokemon;
import com.example.pokemon.integration.pokeapi.ExternalPokemonPage;
import com.example.pokemon.integration.pokeapi.ExternalPokemonSummary;
import com.example.pokemon.integration.pokeapi.PokeApiClient;
import com.example.pokemon.repository.PokemonRepository;
import com.example.pokemon.model.pokemon.Pokemon;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PokemonQueryUseCaseTest {
    private final PokemonRepository repository = mock(PokemonRepository.class);
    private final PokeApiClient api = mock(PokeApiClient.class);
    private final Pokemon pokemon = Pokemon.create(25, "Pikachu", 60, "sprite.png");

    @Test
    void listsPokemonUsingPokeApiPagination() {
        PageRequest pageRequest = PageRequest.of(0, 20);
        when(api.fetchPokemonPage(0, 20)).thenReturn(new ExternalPokemonPage(1,
                List.of(new ExternalPokemonSummary(25, "pikachu", "https://pokeapi.co/api/v2/pokemon/25/")), 0, 20));
        when(api.fetchPokemon(25)).thenReturn(new ExternalPokemon(25, "pikachu", 60, "sprite.png",
                List.of("electric"), List.of("static"), java.util.Map.of("hp", 35)));
        ListPokemonService useCase = new ListPokemonService(api);

        assertThat(useCase.execute(pageRequest).getContent()).singleElement()
                .extracting(Pokemon::name).isEqualTo("pikachu");
        verify(api).fetchPokemonPage(0, 20);
    }

    @Test
    void raisesNotFoundForUnknownPokemon() {
        when(repository.findByPokeApiId(99)).thenReturn(Optional.empty());
        when(api.fetchPokemon(99)).thenReturn(null);
        GetPokemonService useCase = new GetPokemonService(repository, api);

        assertThatThrownBy(() -> useCase.execute(99L))
                .isInstanceOf(PokemonNotFoundException.class);
    }
}
