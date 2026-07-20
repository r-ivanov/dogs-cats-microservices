package com.example.cats.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Wrapper object representing the response from the external Pokemon API")
public record PokemonApiResponse(

  @Schema(
    description = "List of pokemons returned by the external API"
  )
  List<PokemonResponse> results

) {
}