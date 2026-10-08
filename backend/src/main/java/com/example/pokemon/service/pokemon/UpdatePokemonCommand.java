package com.example.pokemon.service.pokemon;

import java.util.List;

public record UpdatePokemonCommand(String localizedName, String region, List<String> internalTags, String notes) {
}
