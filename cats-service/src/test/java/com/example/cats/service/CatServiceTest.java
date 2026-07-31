package com.example.cats.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.cats.client.DogsClient;
import com.example.cats.client.PokemonApiClient;
import com.example.cats.domain.Cat;
import com.example.cats.dto.CatRequest;
import com.example.cats.dto.CatResponse;
import com.example.cats.dto.JokeResponse;
import com.example.cats.dto.PokemonApiResponse;
import com.example.cats.dto.PokemonResponse;
import com.example.cats.mapper.CatMapper;
import com.example.cats.repository.CatRepository;
import com.example.common.exception.ExternalServiceException;
import com.example.common.exception.PhotoStorageException;
import com.example.common.exception.ResourceNotFoundException;

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
  @Mock
  private PokemonApiClient pokemonApiClient;
  @Mock
  private DogsClient dogsClient;

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

    CatResponse response = new CatResponse(1L, "Milo", "Black", 3);

    when(repository.findAll()).thenReturn(List.of(cat));
    when(mapper.toResponse(cat)).thenReturn(response);

    List<CatResponse> result = service.getAll();

    assertEquals(1, result.size());
    assertEquals("Milo", result.get(0).name());

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

    CatResponse response1 = new CatResponse(1L, "Milo", "Black", 3);

    CatResponse response2 = new CatResponse(2L, "Luna", "White", 2);

    when(repository.findAll()).thenReturn(List.of(cat, cat2));
    when(mapper.toResponse(cat)).thenReturn(response1);
    when(mapper.toResponse(cat2)).thenReturn(response2);

    List<CatResponse> result = service.getAll();

    assertEquals(2, result.size());
  }

  @Test
  void getById_shouldReturnCat_whenExists() {

    CatResponse response = new CatResponse(1L, "Milo", "Black", 3);

    when(repository.findById(1L)).thenReturn(Optional.of(cat));
    when(mapper.toResponse(cat)).thenReturn(response);

    CatResponse result = service.getById(1L);

    assertEquals("Milo", result.name());

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

    CatRequest request = new CatRequest("Milo", "Black", 3);

    Cat cat = new Cat();
    cat.setId(1L);

    CatResponse response = new CatResponse(1L, "Milo", "Black", 3);

    when(mapper.toEntity(request)).thenReturn(cat);
    when(repository.save(cat)).thenReturn(cat);
    when(mapper.toResponse(cat)).thenReturn(response);

    CatResponse result = service.create(request);

    assertEquals("Milo", result.name());
  }

  @Test
  void create_shouldCallMapper() {

    CatRequest request = new CatRequest("Test", "Gray", 2);

    when(mapper.toEntity(request)).thenReturn(cat);
    when(repository.save(cat)).thenReturn(cat);
    when(mapper.toResponse(cat)).thenReturn(
      new CatResponse(null, "Test", null, null)
    );

    service.create(request);

    verify(mapper).toEntity(request);
  }

  @Test
  void create_shouldHandleMapping() {

    CatRequest request = new CatRequest("Test", "Black", 2);

    CatResponse response = new CatResponse(null, "Test", null, null);

    when(mapper.toEntity(request)).thenReturn(cat);
    when(repository.save(cat)).thenReturn(cat);
    when(mapper.toResponse(cat)).thenReturn(response);

    CatResponse result = service.create(request);

    assertNotNull(result);
  }

  @Test
  void update_shouldUpdateCat_whenExists() {

    CatRequest request = new CatRequest("NewName", "White", 2);

    CatResponse response = new CatResponse(1L, "NewName", "White", 2);

    when(repository.findById(1L)).thenReturn(Optional.of(cat));
    when(repository.save(any(Cat.class))).thenReturn(cat);
    when(mapper.toResponse(cat)).thenReturn(response);

    CatResponse result = service.update(1L, request);

    assertEquals("NewName", result.name());
  }

  @Test
  void update_shouldThrowException_whenNotFound() {

      CatRequest request = new CatRequest("Test", "Gray", 2);

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

    JokeResponse mockResponse = new JokeResponse(
      "single",
      "Funny joke"
    );

    when(dogsClient.getJoke())
      .thenReturn(mockResponse);

    JokeResponse result = service.getJokeFromDogs();

    assertEquals("single", result.type());
    assertEquals("Funny joke", result.content());
  }

  @Test
  void getJokeFromDogs_shouldThrowException_whenNullResponse() {

    when(dogsClient.getJoke())
      .thenThrow(new ExternalServiceException("Respuesta vacía de Dogs"));

    assertThrows(ExternalServiceException.class, () -> {
      service.getJokeFromDogs();
    });
  }

  @Test
  void getPokemons_shouldReturnList() {

    PokemonResponse pokemon = new PokemonResponse("pikachu", null);

    PokemonApiResponse apiResponse = new PokemonApiResponse(
      List.of(pokemon)
    );

    when(pokemonApiClient.getPokemons(1))
      .thenReturn(apiResponse);

    List<PokemonResponse> result = service.getPokemons(1);

    assertEquals(1, result.size());
    assertEquals("pikachu", result.get(0).name());
  }

  @Test
  void getPokemons_shouldThrowException_whenResponseInvalid() {

    when(pokemonApiClient.getPokemons(1))
    .thenThrow(
      new ExternalServiceException(
        "Respuesta inválida de Pokemon API"
      )
    );

    assertThrows(ExternalServiceException.class, () -> {
      service.getPokemons(1);
    });
  }

  @Test
  void getPokemons_shouldThrowException_whenResponseNull() {

    when(pokemonApiClient.getPokemons(1))
    .thenThrow(
      new ExternalServiceException(
        "Respuesta inválida de Pokemon API"
      )
    );

    assertThrows(ExternalServiceException.class, () -> {
      service.getPokemons(1);
    });
  }

  @Test
  void getJokeFromDogs_shouldThrowException_whenExternalError() {

    when(dogsClient.getJoke())
      .thenThrow(new ExternalServiceException("Error Dogs API"));

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

    CatResponse response = new CatResponse(1L, "Tom", null, null);

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
