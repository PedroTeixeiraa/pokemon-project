package com.example.pokemon.service.pokemon;

import com.example.pokemon.integration.pokeapi.PokeApiClient;
import com.example.pokemon.repository.PokemonRepository;
import com.example.pokemon.model.pokemon.Pokemon;
import com.example.pokemon.integration.pokeapi.ExternalEvolutionChain;
import com.example.pokemon.integration.pokeapi.ExternalPokemon;
import com.example.pokemon.integration.pokeapi.ExternalSpecies;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SyncPokemonServiceTest {
    private final PokeApiClient api = mock(PokeApiClient.class);
    private final PokemonRepository repository = mock(PokemonRepository.class);

    @Test
    void insertsPokemonAndCombinesExternalResources() {
        givenExternalPokemon();
        when(repository.findByPokeApiId(25)).thenReturn(Optional.empty());
        when(repository.save(any(Pokemon.class))).thenAnswer(invocation -> invocation.getArgument(0));
        SyncPokemonService useCase = new SyncPokemonService(api, repository);

        SyncResult result = useCase.execute(new SyncPokemonCommand(List.of(25)));

        assertThat(result).isEqualTo(new SyncResult(1, 0));
        verify(repository).save(any(Pokemon.class));
    }

    @Test
    void updatesExistingPokemonWithoutDiscardingLocalFields() {
        givenExternalPokemon();
        Pokemon existing = Pokemon.create(25, "Pikachu", 60, "old.png")
                .withLocalFields("Pikachu", "Kanto", List.of("favorite"), "Keep me");
        when(repository.findByPokeApiId(25)).thenReturn(Optional.of(existing));
        when(repository.save(any(Pokemon.class))).thenAnswer(invocation -> invocation.getArgument(0));
        SyncPokemonService useCase = new SyncPokemonService(api, repository);

        SyncResult result = useCase.execute(new SyncPokemonCommand(List.of(25)));

        assertThat(result).isEqualTo(new SyncResult(0, 1));
        verify(repository).save(org.mockito.ArgumentMatchers.argThat(pokemon ->
                pokemon.localizedName().equals("Pikachu")
                        && pokemon.internalTags().contains("favorite")
                        && pokemon.notes().equals("Keep me")));
    }

    private void givenExternalPokemon() {
        when(api.fetchPokemon(25)).thenReturn(new ExternalPokemon(25, "pikachu", 60, "sprite.png",
                List.of("electric"), List.of("static"), Map.of("hp", 35)));
        when(api.fetchSpecies(25)).thenReturn(new ExternalSpecies("A description", "chain-url"));
        when(api.fetchEvolutionChain("chain-url")).thenReturn(new ExternalEvolutionChain(List.of("pichu", "pikachu")));
    }
}
