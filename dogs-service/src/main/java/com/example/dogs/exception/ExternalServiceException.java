package com.example.dogs.exception;

public class ExternalServiceException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public ExternalServiceException(String message) {
    super(message);
  }
}
