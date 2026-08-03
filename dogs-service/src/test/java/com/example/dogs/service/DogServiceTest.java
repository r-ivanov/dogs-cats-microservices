package com.example.dogs.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import com.example.common.exception.ExternalServiceException;
import com.example.common.exception.PhotoStorageException;
import com.example.common.exception.ResourceNotFoundException;
import com.example.common.webclient.WebClientSupport;
import com.example.dogs.domain.Dog;
import com.example.dogs.dto.DogRequest;
import com.example.dogs.dto.DogResponse;
import com.example.dogs.dto.JokeApiResponse;
import com.example.dogs.dto.JokeResponse;
import com.example.dogs.dto.PokemonApiResponse;
import com.example.dogs.dto.PokemonResponse;
import com.example.dogs.mapper.DogMapper;
import com.example.dogs.mapper.JokeMapper;
import com.example.dogs.repository.DogRepository;
import com.example.dogs.service.impl.DogService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class DogServiceTest {

  @Mock
  private DogRepository repository;

  @InjectMocks
  private DogService service;

  @Mock
  private DogMapper dogMapper;
  @Mock
  private JokeMapper jokeMapper;

  @Mock
  private WebClientSupport webClientSupport;

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

    DogResponse response = new DogResponse(
      1L,
      "Rocky",
      "Bulldog",
      5,
      "/photos/dogs/1.jpg"
    );

    when(repository.findAll()).thenReturn(List.of(dog));
    when(dogMapper.toResponse(dog)).thenReturn(response);

    List<DogResponse> result = service.getAll();

    assertEquals(1, result.size());
    assertEquals("Rocky", result.get(0).name());

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

    DogResponse response1 = new DogResponse(
      1L,
      "Rocky",
      "Bulldog",
      3
    );

    DogResponse response2 = new DogResponse(
      2L,
      "Roket",
      "Husky",
      2
    );

    when(repository.findAll()).thenReturn(List.of(dog, dog2));
    when(dogMapper.toResponse(dog)).thenReturn(response1);
    when(dogMapper.toResponse(dog2)).thenReturn(response2);

    List<DogResponse> result = service.getAll();

    assertEquals(2, result.size());
  }

  @Test
  void getById_shouldReturnDog_whenExists() {

    DogResponse response = new DogResponse(
      1L,
      "Rocky",
      "Bulldog",
      5
    );

    when(repository.findById(1L)).thenReturn(Optional.of(dog));
    when(dogMapper.toResponse(dog)).thenReturn(response);

    DogResponse result = service.getById(1L);

    assertEquals("Rocky", result.name());

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

    DogRequest request = new DogRequest(
      "Rocky",
      "Bulldog",
      5
    );

    Dog dog = new Dog();
    dog.setId(1L);

    DogResponse response = new DogResponse(
      1L,
      "Rocky",
      "Bulldog",
      5
    );

    when(dogMapper.toEntity(request)).thenReturn(dog);
    when(repository.save(dog)).thenReturn(dog);
    when(dogMapper.toResponse(dog)).thenReturn(response);

    DogResponse result = service.create(request);

    assertEquals("Rocky", result.name());
  }

  @Test
  void create_shouldCallMapper() {

    DogRequest request = new DogRequest(
      "Test",
      "Husky",
      2
    );

    when(dogMapper.toEntity(request)).thenReturn(dog);
    when(repository.save(dog)).thenReturn(dog);
    when(dogMapper.toResponse(dog)).thenReturn(
      new DogResponse(
        null,
        "Test",
        null,
        null
      )
    );

    service.create(request);

    verify(dogMapper).toEntity(request);
  }

  @Test
  void create_shouldHandleMapping() {

    DogRequest request = new DogRequest(
      "Test",
      "Test",
      1
    );

    DogResponse response = new DogResponse(
        null,
        "Test",
        null,
        null
    );

    when(dogMapper.toEntity(request)).thenReturn(dog);
    when(repository.save(dog)).thenReturn(dog);
    when(dogMapper.toResponse(dog)).thenReturn(response);

    DogResponse result = service.create(request);

    assertNotNull(result);
  }

  @Test
  void update_shouldUpdateDog_whenExists() {

    DogRequest request = new DogRequest(
      "NewName",
      "NewName",
      3
    );

    DogResponse response = new DogResponse(
      1L,
      "NewName",
      "NewBreed",
      3
    );

    when(repository.findById(1L)).thenReturn(Optional.of(dog));
    when(repository.save(any(Dog.class))).thenReturn(dog);
    when(dogMapper.toResponse(dog)).thenReturn(response);

    DogResponse result = service.update(1L, request);

    assertEquals("NewName", result.name());
  }

  @Test
  void update_shouldThrowException_whenNotFound() {

    DogRequest request = new DogRequest(
      "Test",
      "Test",
      2
    );

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

    when(webClientSupport.get(
        any(String.class),
        ArgumentMatchers.<ParameterizedTypeReference<List<PokemonApiResponse>>>any(),
        any(String.class),
        any(String.class),
        any(Object[].class)
    ))
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

    when(webClientSupport.get(
        any(String.class),
        eq(JokeApiResponse.class),
        any(String.class),
        any(String.class),
        any(Object[].class)
    ))
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

    DogResponse response = new DogResponse(
      1L,
      "Rocky",
      null,
      null
    );

    when(repository.findById(1L))
      .thenReturn(Optional.of(dog));

    when(dogMapper.toResponse(any(Dog.class)))
      .thenReturn(response);

    DogResponse result = service.uploadPhoto(1L, file);

    assertNotNull(result);

    verify(repository).save(any(Dog.class));
  }

  @Test
  void photoStorageException_shouldKeepCause() {
    RuntimeException cause =
      new RuntimeException("boom");

    PhotoStorageException ex =
      new PhotoStorageException("error", cause);

    assertEquals("error", ex.getMessage());
    assertEquals(cause, ex.getCause());
  }

  @Test
  void uploadPhoto_shouldThrowPhotoStorageException_whenIOException() throws Exception {

    MultipartFile file = mock(MultipartFile.class);

    when(repository.findById(1L))
      .thenReturn(Optional.of(dog));

    when(file.getOriginalFilename())
      .thenReturn("photo.jpg");

    when(file.getInputStream())
      .thenThrow(new IOException("Disk error"));

    assertThrows(
      PhotoStorageException.class,
      () -> service.uploadPhoto(1L, file)
    );
  }
}
