package com.example.dogs.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.dogs.dto.JokeApiResponse;
import com.example.dogs.exception.ExternalServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class JokeApiClientTest {

  @Mock
  private WebClient webClient;

  @InjectMocks
  private JokeApiClient jokeApiClient;

  @Test
  void getRandomJoke_shouldReturnJoke() {

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

    JokeApiResponse mockResponse =
      new JokeApiResponse(
        "single",
        "Funny joke",
        null,
        null
      );

    when(responseSpec.bodyToMono(JokeApiResponse.class))
      .thenReturn(Mono.just(mockResponse));

    JokeApiResponse result = jokeApiClient.getRandomJoke();

    assertEquals("single", result.type());
    assertEquals("Funny joke", result.joke());
  }

  @Test
  void getRandomJoke_shouldThrowException_whenResponseIsNull() {

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

    when(responseSpec.bodyToMono(JokeApiResponse.class))
      .thenReturn(Mono.empty());

    assertThrows(
      ExternalServiceException.class,
      () -> jokeApiClient.getRandomJoke()
    );
  }
}