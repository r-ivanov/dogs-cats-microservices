package com.example.cats.dto;

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
@Schema(description = "Response object representing a joke")
public class JokeResponse {

    @Schema(
        description = "Type of joke (single or twopart)",
        example = "single"
    )
    private String type;

    @Schema(
        description = "Content of the joke",
        example = "Why do programmers prefer dark mode? Because light attracts bugs."
    )
    private String content;
}
