package com.example.dogs.dto;

import io.swagger.v3.oas.annotations.media.Schema;


@Schema(description = "Response object representing a dog")
public record DogResponse(

  @Schema(
    description = "Unique identifier of the dog",
    example = "1"
  )
  Long id,

  @Schema(
    description = "Name of the dog",
    example = "Rocky"
  )
  String name,

  @Schema(
    description = "Breed of the dog",
    example = "Bulldog"
  )
  String breed,

  @Schema(
    description = "Age of the dog",
    example = "5"
  )
  Integer age,

  String photoUrl
) {
  public DogResponse(
    Long id,
    String name,
    String breed,
    Integer age
  ) {
    this(id, name, breed, age, null);
  }
}
