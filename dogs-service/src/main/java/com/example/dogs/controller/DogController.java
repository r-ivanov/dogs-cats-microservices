package com.example.dogs.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;

import com.example.dogs.exception.ErrorResponse;
import com.example.dogs.dto.DogRequest;
import com.example.dogs.dto.DogResponse;
import com.example.dogs.dto.JokeResponse;
import com.example.dogs.dto.PokemonResponse;
import com.example.dogs.service.DogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/api/dogs")
@RequiredArgsConstructor
public class DogController {

  private final DogService service;


  @Operation(
    summary = "Get all dogs",
    description = "Retrieve all dogs"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Dogs retrieved successfully",
      content = @Content(
        schema = @Schema(implementation = DogResponse.class)
      )
    ),
    @ApiResponse(
      responseCode = "500",
      description = "Internal server error",
      content = @Content(
        schema = @Schema(implementation = ErrorResponse.class)
      )
    )
  })
  @GetMapping
  public List<DogResponse> getAll() {
    return service.getAll();
  }


  @Operation(
    summary = "Get dog by ID",
    description = "Retrieve a dog by its ID"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Dog retrieved successfully",
      content = @Content(
        schema = @Schema(implementation = DogResponse.class)
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Dog not found",
      content = @Content(
        schema = @Schema(implementation = ErrorResponse.class)
      )
    ),
    @ApiResponse(
      responseCode = "500",
      description = "Internal server error",
      content = @Content(
        schema = @Schema(implementation = ErrorResponse.class)
      )
    )
  })
  @GetMapping("/{id}")
  public DogResponse getById(@PathVariable Long id) {
    return service.getById(id);
  }


  @Operation(
    summary = "Create dog",
    description = "Create a new dog"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "201",
      description = "Dog created successfully",
      content = @Content(
        schema = @Schema(implementation = DogResponse.class)
      )
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Validation error",
      content = @Content(
        schema = @Schema(implementation = ErrorResponse.class)
      )
    ),
    @ApiResponse(
      responseCode = "500",
      description = "Internal server error",
      content = @Content(
        schema = @Schema(implementation = ErrorResponse.class)
      )
    )
  })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public DogResponse create(@Valid @RequestBody DogRequest request) {
    return service.create(request);
  }


  @Operation(
    summary = "Update dog",
    description = "Update an existing dog"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Dog updated successfully",
      content = @Content(
        schema = @Schema(implementation = DogResponse.class)
      )
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Validation error",
      content = @Content(
        schema = @Schema(implementation = ErrorResponse.class)
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Dog not found",
      content = @Content(
        schema = @Schema(implementation = ErrorResponse.class)
      )
    ),
    @ApiResponse(
      responseCode = "500",
      description = "Internal server error",
      content = @Content(
        schema = @Schema(implementation = ErrorResponse.class)
      )
    )
  })
  @PutMapping("/{id}")
  public DogResponse update(@PathVariable Long id, @Valid @RequestBody DogRequest request) {
    return service.update(id, request);
  }


  @Operation(
    summary = "Delete dog",
    description = "Delete a dog"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "204",
      description = "Dog deleted successfully"
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Dog not found",
      content = @Content(
        schema = @Schema(implementation = ErrorResponse.class)
      )
    ),
    @ApiResponse(
      responseCode = "500",
      description = "Internal server error",
      content = @Content(
        schema = @Schema(implementation = ErrorResponse.class)
      )
    )
  })
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }


  @Operation(
    summary = "Get joke",
    description = "Retrieve a joke from Dogs service"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Joke retrieved successfully",
      content = @Content(
        schema = @Schema(implementation = JokeResponse.class)
      )
    ),
    @ApiResponse(
      responseCode = "502",
      description = "Error from external Joke API",
      content = @Content(
        schema = @Schema(implementation = ErrorResponse.class)
      )
    ),
    @ApiResponse(
      responseCode = "500",
      description = "Internal server error",
      content = @Content(
        schema = @Schema(implementation = ErrorResponse.class)
      )
    )
  })
  @GetMapping("/joke")
  public JokeResponse getJoke() {
    return service.getJoke();
  }


  @Operation(
    summary = "Get pokemons",
    description = "Retrieve pokemons from Cats service"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Pokemons retrieved successfully",
      content = @Content(
        schema = @Schema(implementation = PokemonResponse.class)
      )
    ),
    @ApiResponse(
      responseCode = "400",
      description = "Invalid limit value",
      content = @Content(
        schema = @Schema(implementation = ErrorResponse.class)
      )
    ),
    @ApiResponse(
      responseCode = "502",
      description = "Error from Cats service",
      content = @Content(
        schema = @Schema(implementation = ErrorResponse.class)
      )
    ),
    @ApiResponse(
      responseCode = "500",
      description = "Internal server error",
      content = @Content(
        schema = @Schema(implementation = ErrorResponse.class)
      )
    )
  })
  @GetMapping("/pokemons")
  public List<PokemonResponse> getPokemons(
    @Parameter(description = "Number of pokemons to retrieve", example = "100")
    @RequestParam(name = "limit", defaultValue = "100") @Min(1) @Max(200) int limit) {

    return service.getPokemons(limit);
  }
}
