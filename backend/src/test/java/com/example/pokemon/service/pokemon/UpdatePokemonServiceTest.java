package com.example.pokemon.service.pokemon;

import com.example.pokemon.integration.pokeapi.ExternalPokemon;
import com.example.pokemon.integration.pokeapi.PokeApiClient;
import com.example.pokemon.model.pokemon.Pokemon;
import com.example.pokemon.repository.PokemonRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UpdatePokemonServiceTest {
    private final PokemonRepository repository = mock(PokemonRepository.class);
    private final PokeApiClient api = mock(PokeApiClient.class);

    @Test
    void refusesToUpdatePokemonThatHasNotBeenSynchronized() {
        when(repository.findByPokeApiId(12)).thenReturn(Optional.empty());
        when(api.fetchPokemon(12)).thenReturn(new ExternalPokemon(12, "butterfree", 320, "sprite.png",
                List.of("bug"), List.of("compound-eyes"), java.util.Map.of()));

        UpdatePokemonService service = new UpdatePokemonService(repository, api);

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.execute(12L,
                        new UpdatePokemonCommand("Borboleta", "Kanto", List.of("favorite"), "Keep this note")))
                .isInstanceOf(com.example.pokemon.exception.pokemon.PokemonNotSyncedException.class)
                .hasMessage("Pokemon must be synchronized before local fields can be edited: 12");
    }

    @Test
    void returnsNotFoundWhenPokemonDoesNotExistInPokeApi() {
        when(repository.findByPokeApiId(999)).thenReturn(Optional.empty());
        when(api.fetchPokemon(999)).thenReturn(null);

        UpdatePokemonService service = new UpdatePokemonService(repository, api);

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> service.execute(999L,
                        new UpdatePokemonCommand(null, null, List.of(), null)))
                .isInstanceOf(com.example.pokemon.exception.pokemon.PokemonNotFoundException.class)
                .hasMessage("Pokemon not found: 999");
    }

    @Test
    void updatesLocalFieldsForSynchronizedPokemon() {
        Pokemon existing = Pokemon.create(12, "butterfree", 320, "sprite.png");
        when(repository.findByPokeApiId(12)).thenReturn(Optional.of(existing));
        when(repository.save(org.mockito.ArgumentMatchers.any(Pokemon.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UpdatePokemonService service = new UpdatePokemonService(repository, api);

        Pokemon saved = service.execute(12L,
                new UpdatePokemonCommand("Borboleta", "Kanto", List.of("favorite"), "Keep this note"));

        assertThat(saved.localizedName()).isEqualTo("Borboleta");
        assertThat(saved.region()).isEqualTo("Kanto");
        assertThat(saved.internalTags()).containsExactly("favorite");
        assertThat(saved.notes()).isEqualTo("Keep this note");
    }
}
