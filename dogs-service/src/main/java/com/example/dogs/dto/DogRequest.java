package com.example.dogs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
@Schema(description = "Request object for creating or updating a dog")
public class DogRequest {

    @Schema(
        description = "Name of the dog",
        example = "Rocky"
    )
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(
        description = "Breed of the dog",
        example = "Bulldog"
    )
    @NotBlank(message = "Breed is required")
    private String breed;

    @Schema(
        description = "Age of the dog",
        example = "5",
        minimum = "0",
        maximum = "30"
    )
    @NotNull
    @Min(value = 0, message = "Age must be positive")
    @Max(value = 30, message = "Age seems too high")
    private Integer age;
}
