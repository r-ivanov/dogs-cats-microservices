package com.example.cats.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object representing a joke")
public record JokeResponse(

  @Schema(
    description = "Type of joke (single or twopart)",
    example = "single"
  )
  String type,

  @Schema(
    description = "Content of the joke",
    example = "Why do programmers prefer dark mode? Because light attracts bugs."
  )
  String content

) {
}