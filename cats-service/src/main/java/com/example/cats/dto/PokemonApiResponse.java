package com.example.cats.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Wrapper object representing the response from the external Pokemon API")
public class PokemonApiResponse {

    @Schema(
        description = "List of pokemons returned by the external API"
    )
    private List<PokemonResponse> results;
}