package com.example.dogs.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.dogs.dto.DogRequest;
import com.example.dogs.dto.DogResponse;
import com.example.dogs.dto.JokeResponse;
import com.example.dogs.dto.PokemonResponse;
import com.example.dogs.domain.Dog;
import com.example.dogs.exception.ExternalServiceException;
import com.example.dogs.exception.ResourceNotFoundException;
import com.example.dogs.mapper.DogMapper;
import com.example.dogs.repository.DogRepository;

import reactor.core.publisher.Mono;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(MockitoExtension.class)
class DogServiceTest {

    @Mock
    private DogRepository repository;

    @InjectMocks
    private DogService service;

	@Mock
	private DogMapper mapper;

	@Mock
	private WebClient webClient;

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
    	when(mapper.toResponse(dog)).thenReturn(response);

        List<DogResponse> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals("Rocky", result.get(0).getName());

        verify(repository).findAll();
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
    	when(mapper.toResponse(dog)).thenReturn(response);

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

        when(mapper.toEntity(request)).thenReturn(dog);
        when(repository.save(dog)).thenReturn(dog);
        when(mapper.toResponse(dog)).thenReturn(response);

        DogResponse result = service.create(request);

        assertEquals("Rocky", result.getName());
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

        assertThrows(ResourceNotFoundException.class,
            () -> service.delete(1L));
    }

    @Test
    void update_shouldUpdateDog_whenExists() {

        Dog existing = new Dog();
        existing.setId(1L);

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

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Dog.class))).thenReturn(existing);
        when(mapper.toResponse(existing)).thenReturn(response);

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

        assertThrows(ResourceNotFoundException.class,
                () -> service.update(1L, request));
    }

    @Test
    void getAll_shouldReturnEmptyList() {

        when(repository.findAll()).thenReturn(List.of());

        List<DogResponse> result = service.getAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void create_shouldHandleMapping() {

        DogRequest request = DogRequest.builder()
                .name("Test")
                .breed("Test")
                .age(1)
                .build();

        Dog dog = new Dog();
        DogResponse response = DogResponse.builder().name("Test").build();

        when(mapper.toEntity(request)).thenReturn(dog);
        when(repository.save(dog)).thenReturn(dog);
        when(mapper.toResponse(dog)).thenReturn(response);

        DogResponse result = service.create(request);

        assertNotNull(result);
    }

    @Test
    void getPokemons_shouldReturnList() {

    	WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
    	WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
    	WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

    	when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(anyString(), anyInt())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        List<Map<String, Object>> mockResponse = List.of(
                Map.of("name", "Pikachu")
        );

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn((Mono) Mono.just(mockResponse));

        List<PokemonResponse> result = service.getPokemons(1);

        assertEquals(1, result.size());
        assertEquals("Pikachu", result.get(0).getName());
    }

    @Test
    void getJoke_shouldReturnSingleJoke() {

    	WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
    	WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
    	WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

    	when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        Map<String, Object> mockResponse = Map.of(
                "type", "single",
                "joke", "Funny joke"
        );

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn((Mono) Mono.just(mockResponse));

        JokeResponse result = service.getJoke();

        assertEquals("single", result.getType());
        assertEquals("Funny joke", result.getContent());
    }

    @Test
    void externalServiceException_shouldCreateCorrectly() {

        ExternalServiceException ex =
                new ExternalServiceException("Error externo");

        assertEquals("Error externo", ex.getMessage());
    }
}
