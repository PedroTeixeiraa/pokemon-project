package com.example.pokemon.controller.pokemon;

import com.example.pokemon.service.pokemon.GetPokemonService;
import com.example.pokemon.service.pokemon.DeletePokemonService;
import com.example.pokemon.service.pokemon.ListPokemonService;
import com.example.pokemon.service.pokemon.SyncPokemonCommand;
import com.example.pokemon.service.pokemon.SyncPokemonService;
import com.example.pokemon.service.pokemon.SyncResult;
import com.example.pokemon.service.pokemon.UpdatePokemonCommand;
import com.example.pokemon.service.pokemon.UpdatePokemonService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pokemons")
@Validated
public class PokemonController {
    private final ListPokemonService listPokemonUseCase;
    private final GetPokemonService getPokemonUseCase;
    private final SyncPokemonService syncPokemonUseCase;
    private final UpdatePokemonService updatePokemonUseCase;
    private final DeletePokemonService deletePokemonUseCase;

    public PokemonController(ListPokemonService listPokemonUseCase, GetPokemonService getPokemonUseCase,
                             SyncPokemonService syncPokemonUseCase, UpdatePokemonService updatePokemonUseCase,
                             DeletePokemonService deletePokemonUseCase) {
        this.listPokemonUseCase = listPokemonUseCase;
        this.getPokemonUseCase = getPokemonUseCase;
        this.syncPokemonUseCase = syncPokemonUseCase;
        this.updatePokemonUseCase = updatePokemonUseCase;
        this.deletePokemonUseCase = deletePokemonUseCase;
    }

    @GetMapping
    public PokemonDtos.PageResponse<PokemonDtos.Summary> list(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "20") int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException("Page must be non-negative and size must be between 1 and 100");
        }
        Pageable pageable = PageRequest.of(page, size);
        return PokemonDtos.PageResponse.from(listPokemonUseCase.execute(pageable).map(PokemonDtos.Summary::from));
    }

    @GetMapping("/{id}")
    public PokemonDtos.Details get(@PathVariable Long id) {
        return PokemonDtos.Details.from(getPokemonUseCase.execute(id));
    }

    @PostMapping("/sync")
    public SyncResult sync(@Valid @RequestBody PokemonDtos.SyncRequest request) {
        return syncPokemonUseCase.execute(new SyncPokemonCommand(request.pokeApiIds()));
    }

    @PatchMapping("/{id}")
    public PokemonDtos.Details update(@PathVariable Long id, @Valid @RequestBody PokemonDtos.UpdateRequest request) {
        return PokemonDtos.Details.from(updatePokemonUseCase.execute(id,
                new UpdatePokemonCommand(request.localizedName(), request.region(), request.internalTags(), request.notes())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        deletePokemonUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
