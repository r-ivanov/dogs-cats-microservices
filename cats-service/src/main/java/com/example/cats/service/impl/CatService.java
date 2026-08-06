package com.example.cats.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.cats.domain.Cat;
import com.example.cats.dto.CatRequest;
import com.example.cats.dto.CatResponse;
import com.example.cats.dto.JokeResponse;
import com.example.cats.dto.PokemonApiResponse;
import com.example.cats.dto.PokemonResponse;
import com.example.cats.mapper.CatMapper;
import com.example.cats.repository.CatRepository;
import com.example.cats.service.interfaces.ICatService;
import com.example.common.exception.ExternalServiceException;
import com.example.common.exception.PhotoStorageException;
import com.example.common.exception.ResourceNotFoundException;
import com.example.common.webclient.WebClientSupport;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatService implements ICatService{

  @Value("${services.dogs.url}")
  private String dogsServiceUrl;
  private final CatRepository repository;
  private final CatMapper mapper;
  private final WebClientSupport webClientSupport;
  

  @Override
  @Cacheable("cats")
  public List<CatResponse> getAll() {
    // Comprobar que se ha cacheado y solo se imprime en la primera petición GET
    log.info("Fetching cats from database");
    return repository.findAll()
      .stream()
      .map(mapper::toResponse)
      .collect(Collectors.toList());
  }

  @Override
  @Cacheable(value = "cat", key = "#id")
  public CatResponse getById(Long id) {
    // Comprobar que se ha cacheado y solo se imprime en la primera petición GET
    log.info("Searching cat with id {}", id);
    Cat cat = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cat not found"));

    return mapper.toResponse(cat);
  }

  @Override
  @CacheEvict(value = "cats", allEntries = true)
  public CatResponse create(CatRequest request) {
    log.info("Creating cat with name {}", request.name());
    Cat cat = mapper.toEntity(request);
    return mapper.toResponse(repository.save(cat));
  }

  @Override
  @CacheEvict(value = {"cats", "cat"}, allEntries = true)
  public CatResponse update(Long id, CatRequest request) {
    log.info("Updating cat with id {}", id);
    Cat cat = repository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Cat not found"));

    cat.setName(request.name());
    cat.setColor(request.color());
    cat.setAge(request.age());

    return mapper.toResponse(repository.save(cat));
  }

  @Override
  @CacheEvict(value = {"cats", "cat"}, allEntries = true)
  public void delete(Long id) {
    log.info("Deleting cat with id {}", id);
    if (!repository.existsById(id)) {
      throw new ResourceNotFoundException("Cat not found");
    }
    repository.deleteById(id);
  }

  @Override
  public JokeResponse getJokeFromDogs() {
    log.info("Requesting jokes from dogs-service");
    return webClientSupport.get(
      dogsServiceUrl + "/api/dogs/joke",
      JokeResponse.class,
      "Dogs API",
      "Respuesta vacía de Dogs API"
    );
  }

  @Override
  public List<PokemonResponse> getPokemons(int limit) {
    log.info("Requesting {} pokemons from Pokemon API", limit);
    PokemonApiResponse response =
      webClientSupport.get(
        "https://pokeapi.co/api/v2/pokemon?limit={limit}",
        PokemonApiResponse.class,
        "Pokemon API",
        "Respuesta vacía de Pokemon API",
        limit
      );

    if (response.results() == null) {
      throw new ExternalServiceException("Respuesta inválida de Pokemon API");
    }

    return response.results();
  }

  @Override
  public CatResponse uploadPhoto(Long id, MultipartFile file) {
    log.info("Uploading photo for cat with id {}", id);
    Cat cat = repository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Cat not found"));

    try {

      String originalFilename = file.getOriginalFilename();

      String extension = originalFilename.substring(
        originalFilename.lastIndexOf("."));

      Path uploadDir = Paths.get("uploads/cats");

      Files.createDirectories(uploadDir);

      String filename = id + extension;

      Path destination = uploadDir.resolve(filename);

      Files.copy(
        file.getInputStream(),
        destination,
        StandardCopyOption.REPLACE_EXISTING);

      cat.setPhotoUrl("/photos/cats/" + filename);

      repository.save(cat);

      return mapper.toResponse(cat);

    } catch (IOException e) {
      throw new PhotoStorageException("Error saving photo", e);
    }
  }
}