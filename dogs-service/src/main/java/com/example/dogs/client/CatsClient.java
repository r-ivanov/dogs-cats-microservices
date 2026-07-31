package com.example.dogs.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.common.webclient.WebClientSupport;
import com.example.dogs.dto.PokemonApiResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CatsClient {

  private final WebClient webClient;
  @Value("${services.cats.url}")
  private String catsServiceUrl;

  public List<PokemonApiResponse> getPokemons(int limit) {
    return WebClientSupport.get(
      webClient,
      catsServiceUrl + "/api/cats/pokemons?limit={limit}",
      new ParameterizedTypeReference<List<PokemonApiResponse>>() {},
      "Cats API",
      "Respuesta vacía de Cats API",
      limit
    );
  }
}