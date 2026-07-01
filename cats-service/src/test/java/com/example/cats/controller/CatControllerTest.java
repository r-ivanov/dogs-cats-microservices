package com.example.cats.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.example.cats.dto.*;
import com.example.cats.exception.ErrorResponse;
import com.example.cats.exception.ExternalServiceException;
import com.example.cats.exception.ResourceNotFoundException;
import com.example.cats.service.CatService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CatService service;

    @Test
    void getAll_shouldReturnCats() throws Exception {

      CatResponse cat = CatResponse.builder()
        .id(1L)
        .name("Milo")
        .color("Black")
        .age(3)
        .build();

      when(service.getAll()).thenReturn(List.of(cat));

      mockMvc.perform(get("/api/cats"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("Milo"));
    }

    @Test
    void getById_shouldReturnCat() throws Exception {

      CatResponse cat = CatResponse.builder()
        .id(1L)
        .name("Milo")
        .color("Black")
        .age(3)
        .build();

      when(service.getById(1L)).thenReturn(cat);

      mockMvc.perform(get("/api/cats/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Milo"));
    }

    @Test
    void getById_shouldReturn404_whenNotFound() throws Exception {

      when(service.getById(1L))
        .thenThrow(new ResourceNotFoundException("Not found"));

      mockMvc.perform(get("/api/cats/1"))
        .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldReturn201_whenValid() throws Exception {

      CatRequest request = CatRequest.builder()
        .name("Milo")
        .color("Black")
        .age(3)
        .build();

      CatResponse response = CatResponse.builder()
        .id(1L)
        .name("Milo")
        .color("Black")
        .age(3)
        .build();

      when(service.create(any(CatRequest.class))).thenReturn(response);

      mockMvc.perform(post("/api/cats")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Milo"));
    }

    @Test
    void create_shouldReturn400_whenNullBody() throws Exception {

        mockMvc.perform(post("/api/cats")
          .contentType(MediaType.APPLICATION_JSON)
          .content("{}"))
          .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturn400_whenNameBlank() throws Exception {

      CatRequest request = CatRequest.builder()
        .name("")     // inválido
        .color("Black")
        .age(3)
        .build();

      mockMvc.perform(post("/api/cats")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturn400_whenAgeNegative() throws Exception {

      CatRequest request = CatRequest.builder()
        .name("Milo")
        .color("Black")
        .age(-1)
        .build();

      mockMvc.perform(post("/api/cats")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturn400_whenAgeTooHigh() throws Exception {

      CatRequest request = CatRequest.builder()
        .name("Milo")
        .color("Black")
        .age(30)
        .build();

      mockMvc.perform(post("/api/cats")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
    }

    @Test
    void create_shouldReturnValidationMessage() throws Exception {

      CatRequest request = CatRequest.builder()
        .name("")   // fuerza error
        .color("")  // fuerza error
        .age(-1)    // fuerza error
        .build();

      mockMvc.perform(post("/api/cats")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists())
        .andExpect(jsonPath("$.path").value("/api/cats"));
    }

    @Test
    void create_shouldReturn500_whenServiceFails() throws Exception {

      CatRequest request = CatRequest.builder()
        .name("Milo")
        .color("Black")
        .age(3)
        .build();

      when(service.create(any()))
        .thenThrow(new RuntimeException("error"));

      mockMvc.perform(post("/api/cats")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isInternalServerError());
    }

    @Test
    void update_shouldReturnUpdatedCat() throws Exception {

      CatRequest request = CatRequest.builder()
        .name("NewName")
        .color("White")
        .age(2)
        .build();

      CatResponse response = CatResponse.builder()
        .id(1L)
        .name("NewName")
        .color("White")
        .age(2)
        .build();

      when(service.update(eq(1L), any(CatRequest.class)))
        .thenReturn(response);

      mockMvc.perform(put("/api/cats/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("NewName"));
    }

    @Test
    void update_shouldReturn404_whenNotFound() throws Exception {

      CatRequest request = CatRequest.builder()
        .name("Test")
        .color("Gray")
        .age(2)
        .build();

      when(service.update(eq(1L), any(CatRequest.class)))
        .thenThrow(new ResourceNotFoundException("Not found"));

      mockMvc.perform(put("/api/cats/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
    }

    @Test
    void delete_shouldReturn204() throws Exception {

      mockMvc.perform(delete("/api/cats/1"))
        .andExpect(status().isNoContent());
    }

    @Test
    void delete_shouldReturn404_whenNotFound() throws Exception {

      doThrow(new ResourceNotFoundException("Not found"))
        .when(service).delete(1L);

      mockMvc.perform(delete("/api/cats/1"))
        .andExpect(status().isNotFound());
    }

    @Test
    void getJoke_shouldReturnJoke() throws Exception {

      JokeResponse response = JokeResponse.builder()
        .type("single")
        .content("Funny joke")
        .build();

      when(service.getJokeFromDogs()).thenReturn(response);

      mockMvc.perform(get("/api/cats/joke"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.type").value("single"));
    }

    @Test
    void getJoke_shouldReturn502_whenExternalFails() throws Exception {

      when(service.getJokeFromDogs())
        .thenThrow(new ExternalServiceException("fail"));

      mockMvc.perform(get("/api/cats/joke"))
        .andExpect(status().isBadGateway());
    }

    @Test
    void getPokemons_shouldReturnList() throws Exception {

      PokemonResponse pokemon = new PokemonResponse();
      pokemon.setName("pikachu");

      when(service.getPokemons(10)).thenReturn(List.of(pokemon));

      mockMvc.perform(get("/api/cats/pokemons?limit=10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value("pikachu"));
    }

    @Test
    void getPokemons_shouldReturn502_whenExternalFails() throws Exception {

      when(service.getPokemons(anyInt()))
        .thenThrow(new ExternalServiceException("fail"));

      mockMvc.perform(get("/api/cats/pokemons?limit=10"))
        .andExpect(status().isBadGateway());
    }

    @Test
    void getPokemons_shouldReturn400_whenLimitTooHigh() throws Exception {

      when(service.getPokemons(anyInt()))
        .thenThrow(new jakarta.validation.ConstraintViolationException("invalid", null));

      mockMvc.perform(get("/api/cats/pokemons?limit=300"))
        .andExpect(status().isBadRequest());
    }

    @Test
    void getPokemons_shouldReturn400_whenLimitInvalid() throws Exception {

      when(service.getPokemons(anyInt()))
        .thenThrow(new jakarta.validation.ConstraintViolationException("invalid", null));

      mockMvc.perform(get("/api/cats/pokemons?limit=0"))
        .andExpect(status().isBadRequest());
    }

    @Test
    void resourceNotFoundException_shouldCreateCorrectly() {

      ResourceNotFoundException ex = new ResourceNotFoundException("not found");
      assertEquals("not found", ex.getMessage());
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
}