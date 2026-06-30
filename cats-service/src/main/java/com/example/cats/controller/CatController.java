package com.example.cats.controller;

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

import com.example.cats.dto.CatRequest;
import com.example.cats.dto.CatResponse;
import com.example.cats.dto.JokeResponse;
import com.example.cats.dto.PokemonResponse;
import com.example.cats.exception.ErrorResponse;
import com.example.cats.service.CatService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/api/cats")
@RequiredArgsConstructor
public class CatController {

  private final CatService service;

  @Operation(
    summary = "Get all cats",
    description = "Retrieve all cats"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Cats retrieved successfully",
      content = @Content(
        schema = @Schema(implementation = CatResponse.class)
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
  public List<CatResponse> getAll() {
    return service.getAll();
  }

  @Operation(
    summary = "Get cat by ID",
    description = "Retrieve a cat by its ID"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Cat retrieved successfully",
      content = @Content(
        schema = @Schema(implementation = CatResponse.class)
      )
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Cat not found",
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
  public CatResponse getById(@PathVariable Long id) {
      return service.getById(id);
  }


  @Operation(
    summary = "Create cat",
    description = "Create a new cat"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "201",
      description = "Cat created successfully",
      content = @Content(
        schema = @Schema(implementation = CatResponse.class)
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
  public CatResponse create(@Valid @RequestBody CatRequest request) {
    return service.create(request);
  }


  @Operation(
    summary = "Update cat",
    description = "Update an existing cat"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "200",
      description = "Cat updated successfully",
      content = @Content(
        schema = @Schema(implementation = CatResponse.class)
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
      description = "Cat not found",
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
  public CatResponse update(@PathVariable Long id, @Valid @RequestBody CatRequest request) {
    return service.update(id, request);
  }


  @Operation(
    summary = "Delete cat",
    description = "Delete a cat"
  )
  @ApiResponses(value = {
    @ApiResponse(
      responseCode = "204",
      description = "Cat deleted successfully"
    ),
    @ApiResponse(
      responseCode = "404",
      description = "Cat not found",
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
      description = "Joke retrieved successfully"
    ),
    @ApiResponse(
      responseCode = "502",
      description = "Error from Dogs service",
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
    return service.getJokeFromDogs();
  }


  @Operation(
    summary = "Get pokemons",
    description = "Retrieve pokemons from external API"
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
      description = "Error from external Pokemon API",
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