package com.example.cats.service.interfaces;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.cats.dto.CatRequest;
import com.example.cats.dto.CatResponse;
import com.example.cats.dto.JokeResponse;
import com.example.cats.dto.PokemonResponse;

public interface ICatService {

    List<CatResponse> getAll();

    CatResponse getById(Long id);

    CatResponse create(CatRequest request);

    CatResponse update(Long id, CatRequest request);

    void delete(Long id);

    JokeResponse getJokeFromDogs();

    List<PokemonResponse> getPokemons(int limit);

    CatResponse uploadPhoto(Long id, MultipartFile file);
}