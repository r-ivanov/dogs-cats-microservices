package com.example.dogs.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import com.example.dogs.dto.PokemonApiResponse;
import com.example.dogs.exception.ExternalServiceException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class CatsClientTest {

    @Mock
    private WebClient webClient;

    @InjectMocks
    private CatsClient catsClient;

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

        when(uriSpec.uri(anyString(), anyInt()))
            .thenReturn(headersSpec);

        when(headersSpec.retrieve())
            .thenReturn(responseSpec);

        when(responseSpec.onStatus(any(), any()))
            .thenReturn(responseSpec);

        List<PokemonApiResponse> mockResponse = List.of(
            new PokemonApiResponse("Pikachu")
        );

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
            .thenReturn((Mono) Mono.just(mockResponse));

        List<PokemonApiResponse> result =
            catsClient.getPokemons(1);

        assertEquals(1, result.size());
        assertEquals("Pikachu", result.get(0).name());
    }

    @Test
    void getPokemons_shouldThrowException_whenResponseIsNull() {

        WebClient.RequestHeadersUriSpec uriSpec =
            mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec =
            mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec =
            mock(WebClient.ResponseSpec.class);

        when(webClient.get())
            .thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);

        when(uriSpec.uri(anyString(), anyInt()))
            .thenReturn(headersSpec);

        when(headersSpec.retrieve())
            .thenReturn(responseSpec);

        when(responseSpec.onStatus(any(), any()))
            .thenReturn(responseSpec);

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
            .thenReturn(Mono.empty());

        assertThrows(
            ExternalServiceException.class,
            () -> catsClient.getPokemons(1)
        );
    }
}
