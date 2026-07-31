package com.example.cats.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.cats.dto.JokeResponse;
import com.example.common.webclient.WebClientSupport;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DogsClient {

  private final WebClient webClient;

  @Value("${services.dogs.url}")
  private String dogsServiceUrl;

  public JokeResponse getJoke() {
    return WebClientSupport.get(
      webClient,
      dogsServiceUrl + "/api/dogs/joke",
      JokeResponse.class,
      "Dogs API",
      "Respuesta vacía de Dogs"
    );
  }
}