package com.example.dogs.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object representing a Pokemon")
public record PokemonResponse(

  @Schema(
    description = "Name of the pokemon",
    example = "bulbasaur"
  )
  String name

) {
}
