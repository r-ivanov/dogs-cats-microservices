package com.example.dogs.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.common.webclient.WebClientSupport;
import com.example.dogs.dto.JokeApiResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JokeApiClient {

  private final WebClient webClient;
  public JokeApiResponse getRandomJoke() {
    return WebClientSupport.get(
        webClient,
        "https://v2.jokeapi.dev/joke/Any?lang=es",
        JokeApiResponse.class,
        "Joke API",
        "Respuesta vacía de Joke API"
    );
  }
}