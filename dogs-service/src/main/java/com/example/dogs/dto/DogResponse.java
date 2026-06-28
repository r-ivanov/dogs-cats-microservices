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
@Schema(description = "Response object representing a dog")
public class DogResponse {

    @Schema(
        description = "Unique identifier of the dog",
        example = "1"
    )
    private Long id;

    @Schema(
        description = "Name of the dog",
        example = "Rocky"
    )
    private String name;

    @Schema(
        description = "Breed of the dog",
        example = "Bulldog"
    )
    private String breed;

    @Schema(
        description = "Age of the dog",
        example = "5"
    )
    private Integer age;
}
