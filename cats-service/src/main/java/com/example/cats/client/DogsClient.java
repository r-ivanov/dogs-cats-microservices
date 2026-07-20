package com.example.cats.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.cats.dto.JokeResponse;
import com.example.cats.exception.ExternalServiceException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DogsClient {

  private final WebClient webClient;

  @Value("${services.dogs.url}")
  private String dogsServiceUrl;

  public JokeResponse getJoke() {

    JokeResponse response = webClient.get()
      .uri(dogsServiceUrl + "/api/dogs/joke")
      .retrieve()
      .onStatus(
        status -> status.isError(),
        clientResponse -> clientResponse.bodyToMono(String.class)
          .defaultIfEmpty("Sin mensaje")
          .map(body ->
            new ExternalServiceException(
              "Error Dogs API: "
                + clientResponse.statusCode()
                + " - "
                + body
            )
          )
      )
      .bodyToMono(JokeResponse.class)
      .block();

    if (response == null) {
      throw new ExternalServiceException("Respuesta vacía de Dogs");
    }

    return response;
  }
}