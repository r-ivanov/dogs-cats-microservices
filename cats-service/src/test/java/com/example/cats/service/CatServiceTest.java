package com.example.cats.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.example.cats.CatsServiceApplication;
import com.example.cats.domain.Cat;
import com.example.cats.dto.*;
import com.example.cats.exception.ExternalServiceException;
import com.example.cats.exception.PhotoStorageException;
import com.example.cats.exception.ResourceNotFoundException;
import com.example.cats.mapper.CatMapper;
import com.example.cats.repository.CatRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(MockitoExtension.class)
class CatServiceTest {

  @Mock
  private CatRepository repository;

  @InjectMocks
  private CatService service;

  @Mock
  private CatMapper mapper;

  @Mock
  private WebClient webClient;

  private Cat cat;

  @BeforeEach
  void setUp() {
    cat = new Cat();
    cat.setId(1L);
    cat.setName("Milo");
    cat.setColor("Black");
    cat.setAge(3);
  }

  @Test
  void getAll_shouldReturnCats() {

    CatResponse response = CatResponse.builder()
      .id(1L)
      .name("Milo")
      .color("Black")
      .age(3)
      .build();

    when(repository.findAll()).thenReturn(List.of(cat));
    when(mapper.toResponse(cat)).thenReturn(response);

    List<CatResponse> result = service.getAll();

    assertEquals(1, result.size());
    assertEquals("Milo", result.get(0).getName());

    verify(repository).findAll();
  }

  @Test
  void getAll_shouldReturnEmptyList() {

    when(repository.findAll()).thenReturn(List.of());
    List<CatResponse> result = service.getAll();
    assertTrue(result.isEmpty());
  }

  @Test
  void getAll_shouldReturnMappedList_multipleElements() {

    Cat cat2 = new Cat();
    cat2.setId(2L);
    cat2.setName("Luna");
    cat2.setColor("White");
    cat2.setAge(2);

    CatResponse response1 = CatResponse.builder()
      .id(1L)
      .name("Milo")
      .color("Black")
      .age(3)
      .build();

    CatResponse response2 = CatResponse.builder()
      .id(2L)
      .name("Luna")
      .color("White")
      .age(2)
      .build();

    when(repository.findAll()).thenReturn(List.of(cat, cat2));
    when(mapper.toResponse(cat)).thenReturn(response1);
    when(mapper.toResponse(cat2)).thenReturn(response2);

    List<CatResponse> result = service.getAll();

    assertEquals(2, result.size());
  }

  @Test
  void getById_shouldReturnCat_whenExists() {

    CatResponse response = CatResponse.builder()
      .id(1L)
      .name("Milo")
      .color("Black")
      .age(3)
      .build();

    when(repository.findById(1L)).thenReturn(Optional.of(cat));
    when(mapper.toResponse(cat)).thenReturn(response);

    CatResponse result = service.getById(1L);

    assertEquals("Milo", result.getName());

    verify(repository).findById(1L);
  }

  @Test
  void getById_shouldThrowException_whenNotFound() {

    when(repository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> {
      service.getById(1L);
    });
  }

  @Test
  void create_shouldSaveCat() {

    CatRequest request = CatRequest.builder()
      .name("Milo")
      .color("Black")
      .age(3)
      .build();

    Cat cat = new Cat();
    cat.setId(1L);

    CatResponse response = CatResponse.builder()
      .id(1L)
      .name("Milo")
      .color("Black")
      .age(3)
      .build();

    when(mapper.toEntity(request)).thenReturn(cat);
    when(repository.save(cat)).thenReturn(cat);
    when(mapper.toResponse(cat)).thenReturn(response);

    CatResponse result = service.create(request);

    assertEquals("Milo", result.getName());
  }

  @Test
  void create_shouldCallMapper() {

    CatRequest request = CatRequest.builder()
      .name("Test")
      .color("Gray")
      .age(2)
      .build();

    when(mapper.toEntity(request)).thenReturn(cat);
    when(repository.save(cat)).thenReturn(cat);
    when(mapper.toResponse(cat)).thenReturn(
      CatResponse.builder().name("Test").build()
    );

    service.create(request);

    verify(mapper).toEntity(request);
  }

  @Test
  void create_shouldHandleMapping() {

    CatRequest request = CatRequest.builder()
      .name("Test")
      .color("Black")
      .age(2)
      .build();

    CatResponse response = CatResponse.builder()
      .name("Test")
      .build();

    when(mapper.toEntity(request)).thenReturn(cat);
    when(repository.save(cat)).thenReturn(cat);
    when(mapper.toResponse(cat)).thenReturn(response);

    CatResponse result = service.create(request);

    assertNotNull(result);
  }

  @Test
  void update_shouldUpdateCat_whenExists() {

    CatRequest request = CatRequest.builder()
            .name("NewName")
            .color("White")
            .age(2)
            .build();

    CatResponse response = CatResponse.builder()
            .id(1L)
            .name("NewName")
            .color("White")
            .age(2)
            .build();

    when(repository.findById(1L)).thenReturn(Optional.of(cat));
    when(repository.save(any(Cat.class))).thenReturn(cat);
    when(mapper.toResponse(cat)).thenReturn(response);

    CatResponse result = service.update(1L, request);

    assertEquals("NewName", result.getName());
  }

  @Test
  void update_shouldThrowException_whenNotFound() {

      CatRequest request = CatRequest.builder()
      .name("Test")
      .color("Gray")
      .age(2)
      .build();

      when(repository.findById(1L)).thenReturn(Optional.empty());

      assertThrows(ResourceNotFoundException.class,
        () -> service.update(1L, request));
  }

  @Test
  void delete_shouldDeleteCat_whenExists() {

    when(repository.existsById(1L)).thenReturn(true);
    service.delete(1L);
    verify(repository).deleteById(1L);
  }

  @Test
  void delete_shouldThrowException_whenNotFound() {

    when(repository.existsById(1L)).thenReturn(false);

    assertThrows(ResourceNotFoundException.class,
      () -> service.delete(1L));
  }

  @Test
  void getJokeFromDogs_shouldReturnJoke() {

    WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
    WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
    WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

    when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
    when(uriSpec.uri(anyString())).thenReturn(headersSpec);
    when(headersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

    JokeResponse mockResponse = JokeResponse.builder()
      .type("single")
      .content("Funny joke")
      .build();

    when(responseSpec.bodyToMono(JokeResponse.class))
      .thenReturn(reactor.core.publisher.Mono.just(mockResponse));

    JokeResponse result = service.getJokeFromDogs();

    assertEquals("single", result.getType());
    assertEquals("Funny joke", result.getContent());
  }

  @Test
  void getJokeFromDogs_shouldThrowException_whenNullResponse() {

    WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
    WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
    WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

    when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
    when(uriSpec.uri(anyString())).thenReturn(headersSpec);
    when(headersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

    when(responseSpec.bodyToMono(JokeResponse.class))
      .thenReturn(reactor.core.publisher.Mono.empty());

    assertThrows(ExternalServiceException.class, () -> {
      service.getJokeFromDogs();
    });
  }

  @Test
  void getPokemons_shouldReturnList() {

    WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
    WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
    WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

    when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
    when(uriSpec.uri(any(java.util.function.Function.class))).thenReturn(headersSpec);
    when(headersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

    PokemonResponse pokemon = new PokemonResponse();
    pokemon.setName("pikachu");

    PokemonApiResponse apiResponse = new PokemonApiResponse();
    apiResponse.setResults(List.of(pokemon));

    when(responseSpec.bodyToMono(PokemonApiResponse.class))
      .thenReturn(reactor.core.publisher.Mono.just(apiResponse));

    List<PokemonResponse> result = service.getPokemons(1);

    assertEquals(1, result.size());
    assertEquals("pikachu", result.get(0).getName());
  }

  @Test
  void getPokemons_shouldThrowException_whenResponseInvalid() {

    WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
    WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
    WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

    when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
    when(uriSpec.uri(any(java.util.function.Function.class))).thenReturn(headersSpec);
    when(headersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

    // Simulamos respuesta inválida (sin results)
    when(responseSpec.bodyToMono(PokemonApiResponse.class))
      .thenReturn(reactor.core.publisher.Mono.just(new PokemonApiResponse()));

    assertThrows(ExternalServiceException.class, () -> {
      service.getPokemons(1);
    });
  }

  @Test
  void getPokemons_shouldThrowException_whenResponseNull() {

    WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
    WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
    WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

    when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
    when(uriSpec.uri(any(java.util.function.Function.class))).thenReturn(headersSpec);
    when(headersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

    when(responseSpec.bodyToMono(PokemonApiResponse.class))
      .thenReturn(reactor.core.publisher.Mono.empty());

    assertThrows(ExternalServiceException.class, () -> {
      service.getPokemons(1);
    });
  }

  @Test
  void getJokeFromDogs_shouldThrowException_whenExternalError() {

    WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
    WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
    WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

    when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
    when(uriSpec.uri(anyString())).thenReturn(headersSpec);
    when(headersSpec.retrieve()).thenReturn(responseSpec);

    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

    // simulamos error devolviendo Mono vacío (trigger fallback)
    when(responseSpec.bodyToMono(JokeResponse.class))
      .thenReturn(reactor.core.publisher.Mono.empty());

    assertThrows(ExternalServiceException.class, () -> {
      service.getJokeFromDogs();
    });
  }

  @Test
  void externalServiceException_shouldCreateCorrectly() {

    ExternalServiceException ex =
      new ExternalServiceException("Error externo");

    assertEquals("Error externo", ex.getMessage());
  }

  @Test
  void uploadPhoto_shouldThrowException_whenCatNotFound() {

    MockMultipartFile file = new MockMultipartFile(
      "file",
      "photo.jpg",
      "image/jpeg",
      "test".getBytes());

    when(repository.findById(1L))
      .thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.uploadPhoto(1L, file));
  }

  @Test
  void uploadPhoto_shouldSavePhoto() {

    MockMultipartFile file = new MockMultipartFile(
      "file",
      "photo.jpg",
      "image/jpeg",
      "test".getBytes());

    CatResponse response = CatResponse.builder()
      .id(1L)
      .name("Tom")
      .build();

    when(repository.findById(1L))
      .thenReturn(Optional.of(cat));

    when(mapper.toResponse(any(Cat.class)))
      .thenReturn(response);

    CatResponse result = service.uploadPhoto(1L, file);

    assertNotNull(result);

    verify(repository).save(any(Cat.class));
  }

  @Test
  void photoStorageException_shouldCreateCorrectly() {

    IOException cause = new IOException("Disk error");

    PhotoStorageException ex =
      new PhotoStorageException("Error saving photo", cause);

    assertEquals("Error saving photo", ex.getMessage());
    assertEquals(cause, ex.getCause());
  }
}
