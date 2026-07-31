package com.example.dogs.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.common.exception.ExternalServiceException;
import com.example.dogs.dto.JokeApiResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JokeApiClient {

  private final WebClient webClient;

  public JokeApiResponse getRandomJoke() {

    JokeApiResponse response = webClient.get()
      .uri("https://v2.jokeapi.dev/joke/Any?lang=es")
      .retrieve()
      .onStatus(
        status -> status.isError(),
        clientResponse -> clientResponse.bodyToMono(String.class)
          .defaultIfEmpty("Sin mensaje")
          .map(body ->
            new ExternalServiceException(
              "Error Joke API: "
                + clientResponse.statusCode()
                + " - "
                + body)))
      .bodyToMono(JokeApiResponse.class)
      .block();

    if (response == null) {
      throw new ExternalServiceException("Respuesta vacía de Joke API");
    }

    return response;
  }
}