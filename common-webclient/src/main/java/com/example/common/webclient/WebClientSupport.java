package com.example.common.webclient;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.common.exception.ExternalServiceException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class WebClientSupport {

  private final WebClient webClient;

  public <T> T get(
    String uri,
    Class<T> responseType,
    String serviceName,
    String nullMessage,
    Object... uriVariables) {

    T response = webClient.get()
      .uri(uri, uriVariables)
      .retrieve()
      .onStatus(
        status -> status.isError(),
        clientResponse ->
          handleError(clientResponse, serviceName)
      )
      .bodyToMono(responseType)
      .block();

    if (response == null) {
      throw new ExternalServiceException(nullMessage);
    }

    return response;
  }

  public <T> T get(
    String uri,
    ParameterizedTypeReference<T> responseType,
    String serviceName,
    String nullMessage,
    Object... uriVariables) {

    T response = webClient.get()
      .uri(uri, uriVariables)
      .retrieve()
      .onStatus(
        status -> status.isError(),
        clientResponse ->
          handleError(clientResponse, serviceName)
      )
      .bodyToMono(responseType)
      .block();

    if (response == null) {
      throw new ExternalServiceException(nullMessage);
    }

    return response;
  }

  public Mono<? extends Throwable> handleError(ClientResponse response, String serviceName) {
    return response.bodyToMono(String.class)
      .defaultIfEmpty("Sin mensaje")
      .map(body ->
        new ExternalServiceException(
          "Error "
            + serviceName
            + ": "
            + response.statusCode()
            + " - "
            + body
        )
      );
  }
}