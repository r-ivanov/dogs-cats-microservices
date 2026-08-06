package com.example.common.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;
  private MockHttpServletRequest request;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
    request = new MockHttpServletRequest();
    request.setRequestURI("/api/test");
  }

  @Test
  void handleNotFound_shouldReturn404() {
    ResourceNotFoundException ex =
      new ResourceNotFoundException("Not found");

    ErrorResponse response =
      handler.handleNotFound(ex, request);

    assertEquals(404, response.status());
    assertEquals("Not found", response.message());
    assertEquals("/api/test", response.path());
  }

  @Test
  void handleExternalService_shouldReturn502() {
    ExternalServiceException ex =
      new ExternalServiceException("External error");

    ErrorResponse response =
      handler.handleExternalService(ex, request);

    assertEquals(502, response.status());
    assertEquals("External error", response.message());
    assertEquals("/api/test", response.path());
  }

  @Test
  void handleGeneric_shouldReturn500() {
    RuntimeException ex =
      new RuntimeException("Boom");

    ErrorResponse response =
      handler.handleGeneric(ex, request);

    assertEquals(500, response.status());
    assertEquals("Boom", response.message());
    assertEquals("/api/test", response.path());
  }

  @Test
  void handlePhotoStorage_shouldReturn500() {
    PhotoStorageException ex =
      new PhotoStorageException("Storage error", null);

    ErrorResponse response =
      handler.handlePhotoStorage(ex, request);

    assertEquals(500, response.status());
    assertEquals("Storage error", response.message());
    assertEquals("/api/test", response.path());
  }

  @Test
  void handleConstraintViolation_shouldReturn400() {
    ConstraintViolationException ex =
      new ConstraintViolationException(
        "Invalid parameter",
        Set.<ConstraintViolation<?>>of()
      );

    ErrorResponse response =
      handler.handleConstraintViolation(ex, request);

    assertEquals(400, response.status());
    assertEquals("Invalid parameter", response.message());
    assertEquals("/api/test", response.path());
  }

  @Test
  void handleValidation_shouldReturn400WithFirstErrorMessage() {

    BeanPropertyBindingResult bindingResult =
      new BeanPropertyBindingResult(new Object(), "dog");

    bindingResult.addError(
      new FieldError(
        "dog",
        "name",
        "must not be blank"
      )
    );

    MethodArgumentNotValidException ex =
      new MethodArgumentNotValidException(
        null,
        bindingResult
      );

    ErrorResponse response =
      handler.handleValidation(ex, request);

    assertEquals(400, response.status());
    assertEquals(
      "name: must not be blank",
      response.message()
    );
    assertEquals("/api/test", response.path());
  }

  @Test
  void handleValidation_shouldReturnDefaultMessageWhenNoErrors() {

    BeanPropertyBindingResult bindingResult =
      new BeanPropertyBindingResult(new Object(), "dog");

    MethodArgumentNotValidException ex =
      new MethodArgumentNotValidException(
        null,
        bindingResult
      );

    ErrorResponse response = handler.handleValidation(ex, request);

    assertEquals(400, response.status());
    assertEquals("Validation error", response.message());
    assertEquals("/api/test", response.path());
  }
}