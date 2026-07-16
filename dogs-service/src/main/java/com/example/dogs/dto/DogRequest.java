package com.example.dogs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request object for creating or updating a dog")
public record DogRequest(

  @Schema(
    description = "Name of the dog",
    example = "Rocky"
  )
  @NotBlank(message = "Name is required")
  String name,

  @Schema(
    description = "Breed of the dog",
    example = "Bulldog"
  )
  @NotBlank(message = "Breed is required")
  String breed,

  @Schema(
    description = "Age of the dog",
    example = "5",
    minimum = "0",
    maximum = "30"
  )
  @NotNull
  @Min(value = 0, message = "Age must be positive")
  @Max(value = 30, message = "Age seems too high")
  Integer age

) {
}
