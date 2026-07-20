package com.example.cats.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response object representing a cat")
public record CatResponse(

  @Schema(
    description = "Unique identifier of the cat",
    example = "1"
  )
  Long id,

  @Schema(
    description = "Name of the cat",
    example = "Milo"
  )
  String name,

  @Schema(
    description = "Color of the cat",
    example = "Black"
  )
  String color,

  @Schema(
    description = "Age of the cat",
    example = "3"
  )
  Integer age,

  String photoUrl

) {

  public CatResponse(
    Long id,
    String name,
    String color,
    Integer age
  ) {
    this(id, name, color, age, null);
  }
}