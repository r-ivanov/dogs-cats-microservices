package com.example.dogs.controller;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.example.dogs.dto.DogRequest;
import com.example.dogs.dto.DogResponse;
import com.example.dogs.dto.JokeResponse;
import com.example.dogs.exception.ErrorResponse;
import com.example.dogs.exception.ExternalServiceException;
import com.example.dogs.exception.ResourceNotFoundException;
import com.example.dogs.service.DogService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@AutoConfigureMockMvc
class DogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private DogService service;

    @Test
    void getAll_shouldReturnDogs() throws Exception {

        DogResponse dog = DogResponse.builder()
                .id(1L)
                .name("Rocky")
                .breed("Bulldog")
                .age(5)
                .build();

        when(service.getAll()).thenReturn(List.of(dog));

        mockMvc.perform(get("/api/dogs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Rocky"));
    }

    @Test
    void getById_shouldReturnDog() throws Exception {

        DogResponse dog = DogResponse.builder()
                .id(1L)
                .name("Rocky")
                .breed("Bulldog")
                .age(5)
                .build();

        when(service.getById(1L)).thenReturn(dog);

        mockMvc.perform(get("/api/dogs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Rocky"));
    }

    @Test
    void create_shouldReturn201_whenValid() throws Exception {

        DogRequest request = DogRequest.builder()
                .name("Rocky")
                .breed("Bulldog")
                .age(5)
                .build();

        DogResponse response = DogResponse.builder()
                .id(1L)
                .name("Rocky")
                .breed("Bulldog")
                .age(5)
                .build();

        when(service.create(any(DogRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/dogs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Rocky"));
    }

    @Test
    void delete_shouldReturn204() throws Exception {

        mockMvc.perform(delete("/api/dogs/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getPokemons_shouldReturnList() throws Exception {

        when(service.getPokemons(10)).thenReturn(List.of());

        mockMvc.perform(get("/api/dogs/pokemons?limit=10"))
                .andExpect(status().isOk());
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {

        when(service.getById(1L))
            .thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/api/dogs/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldReturn400_whenNullBody() throws Exception {

        mockMvc.perform(post("/api/dogs")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturn500_whenServiceFails() throws Exception {

    	DogRequest request = DogRequest.builder()
    	        .name("Rocky")
    	        .breed("Bulldog")
    	        .age(5)
    	        .build();
    	
        when(service.create(any()))
            .thenThrow(new RuntimeException("error"));

        mockMvc.perform(post("/api/dogs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getJoke_shouldReturnJoke() throws Exception {

        JokeResponse response = JokeResponse.builder()
                .type("single")
                .content("Funny joke")
                .build();

        when(service.getJoke()).thenReturn(response);

        mockMvc.perform(get("/api/dogs/joke"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("single"));
    }

    @Test
    void create_shouldReturn400_whenNameBlank() throws Exception {

        DogRequest request = DogRequest.builder()
                .name("")
                .breed("Bulldog")
                .age(5)
                .build();

        mockMvc.perform(post("/api/dogs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturn400_whenAgeNegative() throws Exception {

        DogRequest request = DogRequest.builder()
                .name("Rocky")
                .breed("Bulldog")
                .age(-1)
                .build();

        mockMvc.perform(post("/api/dogs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturn400_whenAgeTooHigh() throws Exception {

        DogRequest request = DogRequest.builder()
                .name("Rocky")
                .breed("Bulldog")
                .age(50)
                .build();

        mockMvc.perform(post("/api/dogs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPokemons_shouldReturn400_whenLimitTooHigh() throws Exception {

        when(service.getPokemons(anyInt()))
            .thenThrow(new jakarta.validation.ConstraintViolationException("invalid", null));

        mockMvc.perform(get("/api/dogs/pokemons?limit=300"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPokemons_shouldReturn400_whenLimitInvalid() throws Exception {

        when(service.getPokemons(anyInt()))
            .thenThrow(new jakarta.validation.ConstraintViolationException("invalid", null));

        mockMvc.perform(get("/api/dogs/pokemons?limit=0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getJoke_shouldReturn502_whenExternalFails() throws Exception {

        when(service.getJoke())
            .thenThrow(new ExternalServiceException("fail"));

        mockMvc.perform(get("/api/dogs/joke"))
                .andExpect(status().isBadGateway());
    }

    @Test
    void getPokemons_shouldReturn502_whenExternalFails() throws Exception {

        when(service.getPokemons(10))
            .thenThrow(new ExternalServiceException("fail"));

        mockMvc.perform(get("/api/dogs/pokemons?limit=10"))
                .andExpect(status().isBadGateway());
    }

    @Test
    void errorResponse_shouldBuildCorrectly() {

        ErrorResponse error = ErrorResponse.builder()
                .status(404)
                .message("Not found")
                .path("/api/test")
                .build();

        assertEquals(404, error.getStatus());
    }

    @Test
    void create_shouldReturnValidationMessage() throws Exception {

        DogRequest request = DogRequest.builder()
                .name("")   // fuerza error
                .breed("")  // fuerza error
                .age(-1)    // fuerza error
                .build();

        mockMvc.perform(post("/api/dogs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/dogs"));
    }
}