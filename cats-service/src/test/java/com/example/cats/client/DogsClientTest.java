package com.example.cats.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.cats.dto.JokeResponse;
import com.example.cats.exception.ExternalServiceException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class DogsClientTest {

  @Mock
  private WebClient webClient;

  @InjectMocks
  private DogsClient dogsClient;

  @Test
  void getJoke_shouldReturnJoke() {

    WebClient.RequestHeadersUriSpec uriSpec =
      mock(WebClient.RequestHeadersUriSpec.class);

    WebClient.RequestHeadersSpec headersSpec =
      mock(WebClient.RequestHeadersSpec.class);

    WebClient.ResponseSpec responseSpec =
      mock(WebClient.ResponseSpec.class);

    when(webClient.get())
      .thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);

    when(uriSpec.uri(anyString()))
      .thenReturn(headersSpec);

    when(headersSpec.retrieve())
      .thenReturn(responseSpec);

    when(responseSpec.onStatus(any(), any()))
      .thenReturn(responseSpec);

    JokeResponse mockResponse = new JokeResponse(
      "single",
      "Funny joke"
    );

    when(responseSpec.bodyToMono(JokeResponse.class))
      .thenReturn(Mono.just(mockResponse));

    JokeResponse result = dogsClient.getJoke();

    assertEquals("single", result.type());
    assertEquals("Funny joke", result.content());
  }

  @Test
  void getJoke_shouldThrowException_whenResponseIsNull() {

    WebClient.RequestHeadersUriSpec uriSpec =
      mock(WebClient.RequestHeadersUriSpec.class);

    WebClient.RequestHeadersSpec headersSpec =
      mock(WebClient.RequestHeadersSpec.class);

    WebClient.ResponseSpec responseSpec =
      mock(WebClient.ResponseSpec.class);

    when(webClient.get())
      .thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);

    when(uriSpec.uri(anyString()))
      .thenReturn(headersSpec);

    when(headersSpec.retrieve())
      .thenReturn(responseSpec);

    when(responseSpec.onStatus(any(), any()))
      .thenReturn(responseSpec);

    when(responseSpec.bodyToMono(JokeResponse.class))
      .thenReturn(Mono.empty());

    assertThrows(
      ExternalServiceException.class,
      () -> dogsClient.getJoke()
    );
  }
}