package com.example.dogs.exception;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;


@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ErrorResponse handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {

    return ErrorResponse.builder()
      .timestamp(LocalDateTime.now())
      .status(HttpStatus.NOT_FOUND.value())
      .message(ex.getMessage())
      .path(request.getRequestURI())
      .build();
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ErrorResponse handleGeneric(Exception ex, HttpServletRequest request) {

    // log debug
    ex.printStackTrace();

    return ErrorResponse.builder()
      .timestamp(LocalDateTime.now())
      .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
      .message(ex.getMessage())
      .path(request.getRequestURI())
      .build();
  }

  @ExceptionHandler(ExternalServiceException.class)
  @ResponseStatus(HttpStatus.BAD_GATEWAY)
  @ResponseBody
  public ErrorResponse handleExternalService(ExternalServiceException ex, HttpServletRequest request) {

    return ErrorResponse.builder()
      .timestamp(LocalDateTime.now())
      .status(HttpStatus.BAD_GATEWAY.value())
      .message(ex.getMessage())
      .path(request.getRequestURI())
      .build();
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorResponse handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {

    String message = ex.getBindingResult()
      .getFieldErrors()
      .stream()
      .map(error -> error.getField() + ": " + error.getDefaultMessage())
      .findFirst()
      .orElse("Validation error");

    return ErrorResponse.builder()
      .timestamp(LocalDateTime.now())
      .status(HttpStatus.BAD_REQUEST.value())
      .message(message)
      .path(request.getRequestURI())
      .build();
  }

  @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorResponse handleConstraintViolation(
    jakarta.validation.ConstraintViolationException ex,
    HttpServletRequest request) {

    return ErrorResponse.builder()
      .timestamp(LocalDateTime.now())
      .status(HttpStatus.BAD_REQUEST.value())
      .message("Invalid parameter")
      .path(request.getRequestURI())
      .build();
  }
}