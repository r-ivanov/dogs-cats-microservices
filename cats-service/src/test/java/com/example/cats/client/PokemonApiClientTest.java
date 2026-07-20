package com.example.cats.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import com.example.cats.dto.PokemonApiResponse;
import com.example.cats.dto.PokemonResponse;
import com.example.cats.exception.ExternalServiceException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class PokemonApiClientTest {

  @Mock
  private WebClient webClient;

  @InjectMocks
  private PokemonApiClient pokemonApiClient;

  @Test
  void getPokemons_shouldReturnList() {

    WebClient.RequestHeadersUriSpec uriSpec =
      mock(WebClient.RequestHeadersUriSpec.class);

    WebClient.RequestHeadersSpec headersSpec =
      mock(WebClient.RequestHeadersSpec.class);

    WebClient.ResponseSpec responseSpec =
      mock(WebClient.ResponseSpec.class);

    when(webClient.get())
      .thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);

    when(uriSpec.uri(any(java.util.function.Function.class)))
      .thenReturn(headersSpec);

    when(headersSpec.retrieve())
      .thenReturn(responseSpec);

    when(responseSpec.onStatus(any(), any()))
      .thenReturn(responseSpec);

    PokemonApiResponse apiResponse =
      new PokemonApiResponse(
        List.of(
          new PokemonResponse(
            "pikachu",
            "https://pokeapi.co/api/v2/pokemon/25/"
          )
        )
      );

    when(responseSpec.bodyToMono(PokemonApiResponse.class))
      .thenReturn(Mono.just(apiResponse));

    PokemonApiResponse result =
      pokemonApiClient.getPokemons(1);

    assertEquals(1, result.results().size());
    assertEquals(
      "pikachu",
      result.results().get(0).name()
    );
  }

  @Test
  void getPokemons_shouldThrowException_whenResponseNull() {

    WebClient.RequestHeadersUriSpec uriSpec =
      mock(WebClient.RequestHeadersUriSpec.class);

    WebClient.RequestHeadersSpec headersSpec =
      mock(WebClient.RequestHeadersSpec.class);

    WebClient.ResponseSpec responseSpec =
      mock(WebClient.ResponseSpec.class);

    when(webClient.get())
      .thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);

    when(uriSpec.uri(any(java.util.function.Function.class)))
      .thenReturn(headersSpec);

    when(headersSpec.retrieve())
      .thenReturn(responseSpec);

    when(responseSpec.onStatus(any(), any()))
      .thenReturn(responseSpec);

    when(responseSpec.bodyToMono(PokemonApiResponse.class))
      .thenReturn(Mono.empty());

    assertThrows(
      ExternalServiceException.class,
      () -> pokemonApiClient.getPokemons(1)
    );
  }

  @Test
  void getPokemons_shouldThrowException_whenResultsNull() {

    WebClient.RequestHeadersUriSpec uriSpec =
      mock(WebClient.RequestHeadersUriSpec.class);

    WebClient.RequestHeadersSpec headersSpec =
      mock(WebClient.RequestHeadersSpec.class);

    WebClient.ResponseSpec responseSpec =
      mock(WebClient.ResponseSpec.class);

    when(webClient.get())
      .thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);

    when(uriSpec.uri(any(java.util.function.Function.class)))
      .thenReturn(headersSpec);

    when(headersSpec.retrieve())
      .thenReturn(responseSpec);

    when(responseSpec.onStatus(any(), any()))
      .thenReturn(responseSpec);

    when(responseSpec.bodyToMono(PokemonApiResponse.class))
      .thenReturn(
        Mono.just(
          new PokemonApiResponse(null)
        )
      );

    assertThrows(
      ExternalServiceException.class,
      () -> pokemonApiClient.getPokemons(1)
    );
  }
}