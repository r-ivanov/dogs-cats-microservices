package com.example.cats.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object representing a Pokemon from the external API")
public record PokemonResponse(

  @Schema(
    description = "Name of the pokemon",
    example = "bulbasaur"
  )
  String name,

  @Schema(
    description = "URL with detailed information about the pokemon",
    example = "https://pokeapi.co/api/v2/pokemon/1/"
  )
  String url

) {
}