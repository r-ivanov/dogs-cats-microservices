package com.example.cats.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.cats.domain.Cat;
import com.example.cats.dto.CatRequest;
import com.example.cats.dto.CatResponse;
import com.example.cats.dto.JokeResponse;
import com.example.cats.dto.PokemonResponse;
import com.example.cats.dto.PokemonApiResponse;
import com.example.cats.exception.ExternalServiceException;
import com.example.cats.exception.ResourceNotFoundException;
import com.example.cats.mapper.CatMapper;
import com.example.cats.repository.CatRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CatService {

    private final CatRepository repository;
    private final CatMapper mapper;
    private final WebClient webClient;

	@Value("${services.dogs.url}")
	private String dogsServiceUrl;


    @Cacheable("cats")
    public List<CatResponse> getAll() {
    	// Comprobar que se ha cacheado y solo se imprime en la primera petición GET
    	System.out.println("Fetching from DB");
        return repository.findAll()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "cat", key = "#id")
    public CatResponse getById(Long id) {
    	// Comprobar que se ha cacheado y solo se imprime en la primera petición GET
    	System.out.println("Fetching from DB");
        Cat cat = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cat not found"));

        return mapper.toResponse(cat);
    }

    @CacheEvict(value = "cats", allEntries = true)
    public CatResponse create(CatRequest request) {
    	Cat cat = mapper.toEntity(request);
        return mapper.toResponse(repository.save(cat));
    }

    @CacheEvict(value = {"cats", "cat"}, allEntries = true)
    public CatResponse update(Long id, CatRequest request) {
    	Cat cat = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cat not found"));

        cat.setName(request.getName());
        cat.setColor(request.getColor());
        cat.setAge(request.getAge());

        return mapper.toResponse(repository.save(cat));
    }

    @CacheEvict(value = {"cats", "cat"}, allEntries = true)
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public JokeResponse getJokeFromDogs() {

        JokeResponse response = webClient.get()
                .uri(dogsServiceUrl + "/api/dogs/joke")
                .retrieve()
                .onStatus(status -> status.isError(),
                    clientResponse -> clientResponse.bodyToMono(String.class)
                        .defaultIfEmpty("Sin mensaje")
                        .map(body -> new ExternalServiceException(
                            "Error Dogs API: "
                            + clientResponse.statusCode()
                            + " - " + body
                        ))
                )
                .bodyToMono(JokeResponse.class)
                .block();

        if (response == null) {
            throw new ExternalServiceException("Respuesta vacía de Dogs");
        }

        return response;
    }

    public List<PokemonResponse> getPokemons(int limit) {

    	PokemonApiResponse response = webClient.get()
    			.uri(uriBuilder -> uriBuilder
    			        .scheme("https")
    			        .host("pokeapi.co")
    			        .path("/api/v2/pokemon")
    			        .queryParam("limit", limit)
    			        .build())
    	        .retrieve()
                .onStatus(status -> status.isError(),
                    clientResponse -> clientResponse.bodyToMono(String.class)
                        .defaultIfEmpty("Sin mensaje")
                        .map(body -> new ExternalServiceException(
                            "Error Pokemon API: "
                            + clientResponse.statusCode()
                            + " - " + body
                        ))
                )
                .bodyToMono(PokemonApiResponse.class)
                .block();

    	if (response == null || response.getResults() == null) {
    	    throw new ExternalServiceException("Respuesta inválida de Pokemon API");
    	}

    	return response.getResults();
    }
}