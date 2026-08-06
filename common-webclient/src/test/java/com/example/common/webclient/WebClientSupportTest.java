package com.example.common.webclient;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.common.exception.ExternalServiceException;

class WebClientSupportTest {

  private final WebClientSupport support =
    new WebClientSupport(WebClient.builder().build());

  @Test
  void handleError_shouldCreateExternalServiceException() {

    ClientResponse response =
      ClientResponse.create(HttpStatus.BAD_GATEWAY)
        .body("Service unavailable")
        .build();

    Throwable throwable = support.handleError(response, "pokemon-service").block();

    assertInstanceOf(ExternalServiceException.class,throwable);

    assertTrue(
      throwable.getMessage().contains("pokemon-service")
    );

    assertTrue(
      throwable.getMessage().contains("Service unavailable")
    );
  }

  @Test
  void handleError_shouldUseDefaultMessageWhenBodyEmpty() {

    ClientResponse response =
      ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR)
        .build();

    Throwable throwable =
      support.handleError(response, "joke-service")
        .block();

    assertInstanceOf(ExternalServiceException.class, throwable);

    assertTrue(
      throwable.getMessage()
        .contains("Sin mensaje")
    );
  }
}