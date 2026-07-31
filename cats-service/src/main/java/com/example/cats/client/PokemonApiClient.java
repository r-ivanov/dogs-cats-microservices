package com.example.cats.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.cats.dto.PokemonApiResponse;
import com.example.common.exception.ExternalServiceException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PokemonApiClient {

  private final WebClient webClient;

  public PokemonApiResponse getPokemons(int limit) {

    PokemonApiResponse response = webClient.get()
      .uri(uriBuilder -> uriBuilder
        .scheme("https")
        .host("pokeapi.co")
        .path("/api/v2/pokemon")
        .queryParam("limit", limit)
        .build())
      .retrieve()
      .onStatus(
        status -> status.isError(),
        clientResponse -> clientResponse.bodyToMono(String.class)
          .defaultIfEmpty("Sin mensaje")
          .map(body -> new ExternalServiceException(
            "Error Pokemon API: "
              + clientResponse.statusCode()
              + " - "
              + body
          ))
      )
      .bodyToMono(PokemonApiResponse.class)
      .block();

    if (response == null || response.results() == null) {
      throw new ExternalServiceException("Respuesta inválida de Pokemon API");
    }

    return response;
  }
}