package com.example.dogs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response object representing a Pokemon")
public class PokemonResponse {

  @Schema(
    description = "Name of the pokemon",
    example = "bulbasaur"
  )
  private String name;
}
