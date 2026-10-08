package com.example.pokemon.model.pokemon;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class PokemonTest {
    @Test
    void createsPokemonWithRequiredIdentityAndDefaults() {
        Pokemon pokemon = Pokemon.create(25, "Pikachu", 60, "sprite.png");

        assertThat(pokemon.pokeApiId()).isEqualTo(25);
        assertThat(pokemon.name()).isEqualTo("Pikachu");
        assertThat(pokemon.weight()).isEqualTo(60);
        assertThat(pokemon.localizedName()).isNull();
        assertThat(pokemon.internalTags()).isEmpty();
    }

    @Test
    void rejectsBlankName() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Pokemon.create(25, " ", 60, "sprite.png"))
                .withMessage("Pokemon name must not be blank");
    }

    @Test
    void rejectsNonPositiveWeight() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> Pokemon.create(25, "Pikachu", 0, "sprite.png"))
                .withMessage("Pokemon weight must be positive");
    }
}
