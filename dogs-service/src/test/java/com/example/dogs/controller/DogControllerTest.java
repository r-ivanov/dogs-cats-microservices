package com.example.dogs.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import com.example.common.exception.ExternalServiceException;
import com.example.common.exception.ResourceNotFoundException;
import com.example.dogs.dto.DogRequest;
import com.example.dogs.dto.DogResponse;
import com.example.dogs.dto.JokeResponse;
import com.example.dogs.dto.PokemonResponse;
import com.example.dogs.service.interfaces.IDogService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DogControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @MockitoBean
  private IDogService service;

  @Test
  void getAll_shouldReturnDogs() throws Exception {

    DogResponse dog = new DogResponse(
      1L,
      "Rocky",
      "Bulldog",
      5
    );

    when(service.getAll()).thenReturn(List.of(dog));

    mockMvc.perform(get("/api/dogs"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].name").value("Rocky"));
  }

  @Test
  void getById_shouldReturnDog() throws Exception {

    DogResponse dog = new DogResponse(
      1L,
      "Rocky",
      "Bulldog",
      5
    );

    when(service.getById(1L)).thenReturn(dog);

    mockMvc.perform(get("/api/dogs/1"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("Rocky"));
  }

  @Test
  void getById_shouldReturn404_whenNotFound() throws Exception {

    when(service.getById(1L))
      .thenThrow(new ResourceNotFoundException("Not found"));

    mockMvc.perform(get("/api/dogs/1"))
      .andExpect(status().isNotFound());
  }

  @Test
  void create_shouldReturn201_whenValid() throws Exception {

    DogRequest request = new DogRequest(
      "Rocky",
      "Bulldog",
      5
    );

    DogResponse response = new DogResponse(
      1L,
      "Rocky",
      "Bulldog",
      5
    );

    when(service.create(any(DogRequest.class))).thenReturn(response);

    mockMvc.perform(post("/api/dogs")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.name").value("Rocky"));
  }

  @Test
  void create_shouldReturn400_whenNullBody() throws Exception {

    mockMvc.perform(post("/api/dogs")
      .contentType(MediaType.APPLICATION_JSON)
      .content("{}"))
      .andExpect(status().isBadRequest());
  }

  @Test
  void create_shouldReturn400_whenNameBlank() throws Exception {

    DogRequest request = new DogRequest(
      "", // invalido
      "Bulldog",
      5
    );

    mockMvc.perform(post("/api/dogs")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest());
  }

  @Test
  void create_shouldReturn400_whenAgeNegative() throws Exception {

    DogRequest request = new DogRequest(
      "Rocky",
      "Bulldog",
      -1
    );

    mockMvc.perform(post("/api/dogs")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest());
  }

  @Test
  void create_shouldReturn400_whenAgeTooHigh() throws Exception {

    DogRequest request = new DogRequest(
      "Rocky",
      "Bulldog",
      50
    );

    mockMvc.perform(post("/api/dogs")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest());
  }

  @Test
  void create_shouldReturnValidationMessage() throws Exception {

    DogRequest request = new DogRequest(
      "", // fuerza error
      "", // fuerza error
      -1  // fuerza error
    );

    mockMvc.perform(post("/api/dogs")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").exists())
      .andExpect(jsonPath("$.path").value("/api/dogs"));
  }

  @Test
  void create_shouldReturn500_whenServiceFails() throws Exception {

    DogRequest request = new DogRequest(
      "Rocky",
      "Bulldog",
      5
    );

    when(service.create(any()))
      .thenThrow(new RuntimeException("error"));

    mockMvc.perform(post("/api/dogs")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isInternalServerError());
  }

  @Test
  void update_shouldReturnUpdatedDog() throws Exception {

    DogRequest request = new DogRequest(
      "NewName",
      "White",
      2
    );

    DogResponse response = new DogResponse(
      1L,
      "NewName",
      "White",
      2
    );

    when(service.update(eq(1L), any(DogRequest.class)))
      .thenReturn(response);

    mockMvc.perform(put("/api/dogs/1")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.name").value("NewName"));
  }

  @Test
  void update_shouldReturn404_whenNotFound() throws Exception {

    DogRequest request = new DogRequest(
      "Test",
      "Husky",
      2
    );

    when(service.update(eq(1L), any(DogRequest.class)))
      .thenThrow(new ResourceNotFoundException("Not found"));

    mockMvc.perform(put("/api/dogs/1")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isNotFound());
  }

  @Test
  void delete_shouldReturn204() throws Exception {

    mockMvc.perform(delete("/api/dogs/1"))
      .andExpect(status().isNoContent());
  }

  @Test
  void delete_shouldReturn404_whenNotFound() throws Exception {

    doThrow(new ResourceNotFoundException("Not found"))
      .when(service).delete(1L);

    mockMvc.perform(delete("/api/dogs/1"))
      .andExpect(status().isNotFound());
  }

  @Test
  void getJoke_shouldReturnJoke() throws Exception {

    JokeResponse response = new JokeResponse(
      "single",
      "Funny joke"
    );

    when(service.getJoke()).thenReturn(response);

    mockMvc.perform(get("/api/dogs/joke"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.type").value("single"));
  }

  @Test
  void getJoke_shouldReturn502_whenExternalFails() throws Exception {

    when(service.getJoke())
      .thenThrow(new ExternalServiceException("fail"));

    mockMvc.perform(get("/api/dogs/joke"))
      .andExpect(status().isBadGateway());
  }

  @Test
  void getPokemons_shouldReturnList() throws Exception {

    PokemonResponse pokemon = new PokemonResponse("pikachu");

    when(service.getPokemons(10)).thenReturn(List.of(pokemon));

    mockMvc.perform(get("/api/dogs/pokemons?limit=10"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].name").value("pikachu"));
  }

  @Test
  void getPokemons_shouldReturn502_whenExternalFails() throws Exception {

    when(service.getPokemons(anyInt()))
      .thenThrow(new ExternalServiceException("fail"));

    mockMvc.perform(get("/api/dogs/pokemons?limit=10"))
      .andExpect(status().isBadGateway());
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
  void uploadPhoto_shouldReturn200() throws Exception {

    DogResponse response = new DogResponse(
      1L,
      "Rocky",
      "Bulldog",
      5,
      "/photos/dogs/1.jpg"
    );

    MockMultipartFile file = new MockMultipartFile(
      "file",
      "photo.jpg",
      "image/jpeg",
      "fake-image".getBytes());

    when(service.uploadPhoto(eq(1L), any(MultipartFile.class)))
      .thenReturn(response);

    mockMvc.perform(
      multipart("/api/dogs/1/photo")
        .file(file))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.photoUrl")
        .value("/photos/dogs/1.jpg"));
  }
}