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
@Schema(description = "Response object representing a cat")
public class CatResponse {

  @Schema(
    description = "Unique identifier of the cat",
    example = "1"
  )
  private Long id;

  @Schema(
    description = "Name of the cat",
    example = "Milo"
  )
  private String name;

  @Schema(
    description = "Color of the cat",
    example = "Black"
  )
  private String color;

  @Schema(
    description = "Age of the cat",
    example = "3"
  )
  private Integer age;
  
  private String photoUrl;
}
