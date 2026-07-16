package com.example.dogs.client;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.dogs.dto.PokemonApiResponse;
import com.example.dogs.exception.ExternalServiceException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CatsClient {

  private final WebClient webClient;
  @Value("${services.cats.url}")
  private String catsServiceUrl;

  public List<PokemonApiResponse> getPokemons(int limit) {

    List<PokemonApiResponse> response = webClient.get()
      .uri(catsServiceUrl + "/api/cats/pokemons?limit={limit}", limit)
      .retrieve()
      .onStatus(
        status -> status.isError(),
        clientResponse -> clientResponse.bodyToMono(String.class)
          .defaultIfEmpty("Sin mensaje")
          .map(body -> new ExternalServiceException(
              "Error Cats API: "
                + clientResponse.statusCode()
                + " - "
                + body)))
      .bodyToMono(new ParameterizedTypeReference<List<PokemonApiResponse>>() {})
      .block();

    if (response == null) {
      throw new ExternalServiceException("Respuesta vacía de Cats API");
    }

    return response;
  }
}