package com.example.dogs.service.interfaces;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.dogs.dto.DogRequest;
import com.example.dogs.dto.DogResponse;
import com.example.dogs.dto.JokeResponse;
import com.example.dogs.dto.PokemonResponse;

public interface IDogService {

    List<DogResponse> getAll();

    DogResponse getById(Long id);

    DogResponse create(DogRequest request);

    DogResponse update(Long id, DogRequest request);

    void delete(Long id);

    JokeResponse getJoke();

    List<PokemonResponse> getPokemons(int limit);

    DogResponse uploadPhoto(Long id, MultipartFile file);
}