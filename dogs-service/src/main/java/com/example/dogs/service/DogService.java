package com.example.dogs.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DogService {

  @Value("${services.cats.url}")
  private String catsServiceUrl;
  private final DogRepository repository;
  private final DogMapper mapper;
  private final JokeMapper jokeMapper;
  private final WebClientSupport webClientSupport;


  @Cacheable("dogs")
  public List<DogResponse> getAll() {
    // Comprobar que se ha cacheado y solo se imprime en la primera petición GET
    System.out.println("Fetching from DB");
    return repository.findAll()
      .stream()
      .map(mapper::toResponse)
      .collect(Collectors.toList());
  }

  @Cacheable(value = "dog", key = "#id")
  public DogResponse getById(Long id) {
    // Comprobar que se ha cacheado y solo se imprime en la primera petición GET
    System.out.println("Fetching from DB");
    Dog dog = repository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Dog not found"));

    return mapper.toResponse(dog);
  }

  @CacheEvict(value = "dogs", allEntries = true)
  public DogResponse create(DogRequest request) {
    Dog dog = mapper.toEntity(request);
    return mapper.toResponse(repository.save(dog));
  }

  @CacheEvict(value = {"dogs", "dog"}, allEntries = true)
  public DogResponse update(Long id, DogRequest request) {
    Dog dog = repository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Dog not found"));

    dog.setName(request.name());
    dog.setBreed(request.breed());
    dog.setAge(request.age());

    return mapper.toResponse(repository.save(dog));
  }

  @CacheEvict(value = {"dogs", "dog"}, allEntries = true)
  public void delete(Long id) {
    if (!repository.existsById(id)) {
      throw new ResourceNotFoundException("Dog not found");
    }
    repository.deleteById(id);
  }

  public JokeResponse getJoke() {

    JokeApiResponse response =
      webClientSupport.get(
        "https://v2.jokeapi.dev/joke/Any?lang=es",
        JokeApiResponse.class,
        "Joke API",
        "Respuesta vacía de Joke API"
      );

    return jokeMapper.toJokeResponse(response);
  }

  public List<PokemonResponse> getPokemons(int limit) {

    List<PokemonApiResponse> response =
      webClientSupport.get(
        catsServiceUrl + "/api/cats/pokemons?limit={limit}",
        new ParameterizedTypeReference<List<PokemonApiResponse>>() {},
        "Cats API",
        "Respuesta vacía de Cats API",
        limit
      );

    return response.stream()
      .map(p -> new PokemonResponse(
        p.name()
      ))
      .toList();
}

  public DogResponse uploadPhoto(Long id, MultipartFile file) {

    Dog dog = repository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Dog not found"));

    try {

      String originalFilename = file.getOriginalFilename();

      String extension = originalFilename.substring(
        originalFilename.lastIndexOf("."));

      Path uploadDir = Paths.get("uploads/dogs");

      Files.createDirectories(uploadDir);

      String filename = id + extension;

      Path destination = uploadDir.resolve(filename);

      Files.copy(
        file.getInputStream(),
        destination,
        StandardCopyOption.REPLACE_EXISTING);

      dog.setPhotoUrl("/photos/dogs/" + filename);

      repository.save(dog);

      return mapper.toResponse(dog);

    } catch (IOException e) {
      throw new PhotoStorageException( "Error saving photo", e);
    }
  }

}