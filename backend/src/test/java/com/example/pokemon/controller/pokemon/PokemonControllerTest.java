package com.example.pokemon.controller.pokemon;

import com.example.pokemon.service.pokemon.GetPokemonService;
import com.example.pokemon.service.pokemon.ListPokemonService;
import com.example.pokemon.exception.pokemon.PokemonNotFoundException;
import com.example.pokemon.service.pokemon.SyncPokemonService;
import com.example.pokemon.service.pokemon.SyncResult;
import com.example.pokemon.service.pokemon.UpdatePokemonService;
import com.example.pokemon.model.pokemon.Pokemon;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PokemonController.class)
@AutoConfigureMockMvc(addFilters = false)
class PokemonControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ListPokemonService listPokemonUseCase;

    @MockBean
    private GetPokemonService getPokemonUseCase;

    @MockBean
    private SyncPokemonService syncPokemonUseCase;

    @MockBean
    private UpdatePokemonService updatePokemonUseCase;

    @MockBean
    private com.example.pokemon.service.pokemon.DeletePokemonService deletePokemonUseCase;

    @Test
    void listsPokemonWithPagination() throws Exception {
        Pokemon pokemon = Pokemon.create(25, "Pikachu", 60, "sprite.png");
        when(listPokemonUseCase.execute(any())).thenReturn(new PageImpl<>(List.of(pokemon), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/pokemons?page=0&size=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Pikachu"));
    }

    @Test
    void returnsNotFoundForUnknownPokemon() throws Exception {
        when(getPokemonUseCase.execute(99L)).thenThrow(new PokemonNotFoundException(99L));

        mockMvc.perform(get("/api/pokemons/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void synchronizesRequestedPokemon() throws Exception {
        when(syncPokemonUseCase.execute(any())).thenReturn(new SyncResult(1, 0));

        mockMvc.perform(post("/api/pokemons/sync")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"pokeApiIds\":[25]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inserted").value(1));
    }

    @Test
    void rejectsOversizedLocalField() throws Exception {
        String oversizedName = "x".repeat(101);

        mockMvc.perform(patch("/api/pokemons/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"localizedName\":\"" + oversizedName + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void rejectsMalformedLocalFieldsPayload() throws Exception {
        mockMvc.perform(patch("/api/pokemons/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"internalTags\":\"not-an-array\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void deletesSynchronizedPokemon() throws Exception {
        mockMvc.perform(delete("/api/pokemons/25"))
                .andExpect(status().isNoContent());
    }
}
