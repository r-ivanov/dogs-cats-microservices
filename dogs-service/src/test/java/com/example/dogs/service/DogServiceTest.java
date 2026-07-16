package com.example.dogs.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.dogs.dto.DogRequest;
import com.example.dogs.dto.DogResponse;
import com.example.dogs.dto.JokeResponse;
import com.example.dogs.dto.PokemonApiResponse;
import com.example.dogs.dto.JokeApiResponse;
import com.example.dogs.dto.PokemonResponse;
import com.example.dogs.client.CatsClient;
import com.example.dogs.client.JokeApiClient;
import com.example.dogs.domain.Dog;
import com.example.dogs.exception.ExternalServiceException;
import com.example.dogs.exception.ResourceNotFoundException;
import com.example.dogs.mapper.DogMapper;
import com.example.dogs.mapper.JokeMapper;
import com.example.dogs.repository.DogRepository;

import reactor.core.publisher.Mono;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(MockitoExtension.class)
class DogServiceTest {

  @Mock
  private DogRepository repository;

  @InjectMocks
  private DogService service;
  
  @Mock
  private CatsClient catsClient;

  @Mock
  private DogMapper dogMapper;
  @Mock
  private JokeMapper jokeMapper;

  @Mock
  private WebClient webClient;
  @Mock
  private JokeApiClient jokeApiClient;

  private Dog dog;

  @BeforeEach
  void setUp() {
    dog = new Dog();
    dog.setId(1L);
    dog.setName("Rocky");
    dog.setBreed("Bulldog");
    dog.setAge(5);
  }

  @Test
  void getAll_shouldReturnDogs() {

    DogResponse response = DogResponse.builder()
      .id(1L)
      .name("Rocky")
      .breed("Bulldog")
      .age(5)
      .build();

    when(repository.findAll()).thenReturn(List.of(dog));
    when(dogMapper.toResponse(dog)).thenReturn(response);

    List<DogResponse> result = service.getAll();

    assertEquals(1, result.size());
    assertEquals("Rocky", result.get(0).getName());

    verify(repository).findAll();
  }

  @Test
  void getAll_shouldReturnEmptyList() {

    when(repository.findAll()).thenReturn(List.of());
    List<DogResponse> result = service.getAll();
    assertTrue(result.isEmpty());
  }

  @Test
  void getAll_shouldReturnMappedList_multipleElements() {

    Dog dog2 = new Dog();
    dog2.setId(2L);
    dog2.setName("Roket");
    dog2.setBreed("Husky");
    dog2.setAge(2);

    DogResponse response1 = DogResponse.builder()
      .id(1L)
      .name("Rocky")
      .breed("Bulldog")
      .age(3)
      .build();

    DogResponse response2 = DogResponse.builder()
      .id(2L)
      .name("Roket")
      .breed("Husky")
      .age(2)
      .build();

    when(repository.findAll()).thenReturn(List.of(dog, dog2));
    when(dogMapper.toResponse(dog)).thenReturn(response1);
    when(dogMapper.toResponse(dog2)).thenReturn(response2);

    List<DogResponse> result = service.getAll();

    assertEquals(2, result.size());
  }

  @Test
  void getById_shouldReturnDog_whenExists() {

    DogResponse response = DogResponse.builder()
      .id(1L)
      .name("Rocky")
      .breed("Bulldog")
      .age(5)
      .build();

    when(repository.findById(1L)).thenReturn(Optional.of(dog));
    when(dogMapper.toResponse(dog)).thenReturn(response);

    DogResponse result = service.getById(1L);

    assertEquals("Rocky", result.getName());

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
  void create_shouldSaveDog() {

    DogRequest request = DogRequest.builder()
      .name("Rocky")
      .breed("Bulldog")
      .age(5)
      .build();

    Dog dog = new Dog();
    dog.setId(1L);

    DogResponse response = DogResponse.builder()
      .id(1L)
      .name("Rocky")
      .breed("Bulldog")
      .age(5)
      .build();

    when(dogMapper.toEntity(request)).thenReturn(dog);
    when(repository.save(dog)).thenReturn(dog);
    when(dogMapper.toResponse(dog)).thenReturn(response);

    DogResponse result = service.create(request);

    assertEquals("Rocky", result.getName());
  }

  @Test
  void create_shouldCallMapper() {

    DogRequest request = DogRequest.builder()
      .name("Test")
      .breed("Husky")
      .age(2)
      .build();

    when(dogMapper.toEntity(request)).thenReturn(dog);
    when(repository.save(dog)).thenReturn(dog);
    when(dogMapper.toResponse(dog)).thenReturn(
      DogResponse.builder().name("Test").build()
    );

    service.create(request);

    verify(dogMapper).toEntity(request);
  }

  @Test
  void create_shouldHandleMapping() {

    DogRequest request = DogRequest.builder()
      .name("Test")
      .breed("Test")
      .age(1)
      .build();

    DogResponse response = DogResponse.builder()
      .name("Test")
      .build();

    when(dogMapper.toEntity(request)).thenReturn(dog);
    when(repository.save(dog)).thenReturn(dog);
    when(dogMapper.toResponse(dog)).thenReturn(response);

    DogResponse result = service.create(request);

    assertNotNull(result);
  }

  @Test
  void update_shouldUpdateDog_whenExists() {

    DogRequest request = DogRequest.builder()
      .name("NewName")
      .breed("NewBreed")
      .age(3)
      .build();

    DogResponse response = DogResponse.builder()
      .id(1L)
      .name("NewName")
      .breed("NewBreed")
      .age(3)
      .build();

    when(repository.findById(1L)).thenReturn(Optional.of(dog));
    when(repository.save(any(Dog.class))).thenReturn(dog);
    when(dogMapper.toResponse(dog)).thenReturn(response);

    DogResponse result = service.update(1L, request);

    assertEquals("NewName", result.getName());
  }

  @Test
  void update_shouldThrowException_whenNotFound() {

    DogRequest request = DogRequest.builder()
      .name("Test")
      .breed("Test")
      .age(2)
      .build();

    when(repository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> service.update(1L, request));
  }

  @Test
  void delete_shouldDeleteDog_whenExists() {

    when(repository.existsById(1L)).thenReturn(true);
    service.delete(1L);
    verify(repository).deleteById(1L);
  }

  @Test
  void delete_shouldThrowException_whenNotFound() {

    when(repository.existsById(1L)).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
  }

  @Test
  void getPokemons_shouldReturnList() {

    List<PokemonApiResponse> mockResponse = List.of(
        new PokemonApiResponse("Pikachu")
    );

    when(catsClient.getPokemons(eq(1)))
      .thenReturn(mockResponse);

    List<PokemonResponse> result = service.getPokemons(1);

    assertEquals(1, result.size());
    assertEquals("Pikachu", result.get(0).name());
  }

  @Test
  void getJoke_shouldReturnSingleJoke() {

    JokeApiResponse mockResponse =
      new JokeApiResponse(
        "single",
        "Funny joke",
        null,
        null
      );

    when(jokeApiClient.getRandomJoke())
      .thenReturn(mockResponse);

    when(jokeMapper.toJokeResponse(mockResponse))
      .thenReturn(
        new JokeResponse(
            "single",
            "Funny joke"
        )
      );

    JokeResponse result = service.getJoke();

    assertEquals("single", result.type());
    assertEquals("Funny joke", result.content());
  }

  @Test
  void externalServiceException_shouldCreateCorrectly() {

    ExternalServiceException ex = new ExternalServiceException("Error externo");

    assertEquals("Error externo", ex.getMessage());
  }

  @Test
  void uploadPhoto_shouldThrowException_whenDogNotFound() {

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

      DogResponse response = DogResponse.builder()
        .id(1L)
        .name("Rocky")
        .build();

      when(repository.findById(1L))
        .thenReturn(Optional.of(dog));

      when(dogMapper.toResponse(any(Dog.class)))
        .thenReturn(response);

      DogResponse result = service.uploadPhoto(1L, file);

      assertNotNull(result);

      verify(repository).save(any(Dog.class));
  }
}
