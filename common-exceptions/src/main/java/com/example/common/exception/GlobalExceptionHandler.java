package com.example.common.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ResponseBody
  public ErrorResponse handleNotFound(ResourceNotFoundException ex, 
                                      HttpServletRequest request) {

    return new ErrorResponse(
      LocalDateTime.now(),
      HttpStatus.NOT_FOUND.value(),
      ex.getMessage(),
      request.getRequestURI()
    );
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ErrorResponse handleGeneric(Exception ex,
                                     HttpServletRequest request) {

    // log debug
    log.error("Unexpected error", ex);
    return new ErrorResponse(
      LocalDateTime.now(),
      HttpStatus.INTERNAL_SERVER_ERROR.value(),
      ex.getMessage(),
      request.getRequestURI()
    );
  }

  @ExceptionHandler(ExternalServiceException.class)
  @ResponseStatus(HttpStatus.BAD_GATEWAY)
  @ResponseBody
  public ErrorResponse handleExternalService(ExternalServiceException ex, 
                                             HttpServletRequest request) {
    return new ErrorResponse(
      LocalDateTime.now(),
      HttpStatus.BAD_GATEWAY.value(),
      ex.getMessage(),
      request.getRequestURI()
    );
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorResponse handleValidation(MethodArgumentNotValidException ex,
                                        HttpServletRequest request) {

    String message = ex.getBindingResult()
      .getFieldErrors()
      .stream()
      .map(error -> error.getField() + ": " + error.getDefaultMessage())
      .findFirst()
      .orElse("Validation error");

    return new ErrorResponse(
      LocalDateTime.now(),
      HttpStatus.BAD_REQUEST.value(),
      message,
      request.getRequestURI()
    );
  }

  @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ErrorResponse handleConstraintViolation(jakarta.validation.ConstraintViolationException ex,
                                                 HttpServletRequest request) {

    return new ErrorResponse(
      LocalDateTime.now(),
      HttpStatus.BAD_REQUEST.value(),
      "Invalid parameter",
      request.getRequestURI()
    );
  }

  @ExceptionHandler(PhotoStorageException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ErrorResponse handlePhotoStorage(PhotoStorageException ex,
                                          HttpServletRequest request) {

    return new ErrorResponse(
      LocalDateTime.now(),
      HttpStatus.INTERNAL_SERVER_ERROR.value(),
      ex.getMessage(),
      request.getRequestURI()
    );
  }
}