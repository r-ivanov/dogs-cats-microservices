package com.example.cats.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request object for creating or updating a cat")
public record CatRequest(

  @Schema(
    description = "Name of the cat",
    example = "Milo"
  )
  @NotBlank(message = "Name is required")
  String name,

  @Schema(
    description = "Color of the cat",
    example = "Black"
  )
  @NotBlank(message = "Color is required")
  String color,

  @Schema(
    description = "Age of the cat",
    example = "3",
    minimum = "0",
    maximum = "25"
  )
  @NotNull
  @Min(value = 0, message = "Age must be positive")
  @Max(value = 25, message = "Age seems too high")
  Integer age

) {
}
