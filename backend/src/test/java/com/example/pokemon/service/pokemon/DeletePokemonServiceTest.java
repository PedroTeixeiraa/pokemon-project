package com.example.pokemon.service.pokemon;

import com.example.pokemon.exception.pokemon.PokemonNotFoundException;
import com.example.pokemon.model.pokemon.Pokemon;
import com.example.pokemon.repository.PokemonRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeletePokemonServiceTest {
    private final PokemonRepository repository = mock(PokemonRepository.class);

    @Test
    void deletesSynchronizedPokemonByPokeApiId() {
        when(repository.findByPokeApiId(25)).thenReturn(Optional.of(Pokemon.create(25, "pikachu", 60, "sprite.png")));

        new DeletePokemonService(repository).execute(25L);

        verify(repository).deleteByPokeApiId(25);
    }

    @Test
    void rejectsDeletingPokemonThatIsNotStoredLocally() {
        when(repository.findByPokeApiId(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new DeletePokemonService(repository).execute(999L))
                .isInstanceOf(PokemonNotFoundException.class);
    }
}
