package com.example.cats.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.cats.dto.PokemonApiResponse;
import com.example.common.exception.ExternalServiceException;
import com.example.common.webclient.WebClientSupport;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PokemonApiClient {

  private final WebClient webClient;

  public PokemonApiResponse getPokemons(int limit) {
    PokemonApiResponse response =
      WebClientSupport.get(
          webClient,
          "https://pokeapi.co/api/v2/pokemon?limit={limit}",
          PokemonApiResponse.class,
          "Pokemon API",
          "Respuesta vacía de Pokemon API",
          limit
      );

    if (response.results() == null) {
      throw new ExternalServiceException("Respuesta inválida de Pokemon API");
    }

    return response;
  }
}