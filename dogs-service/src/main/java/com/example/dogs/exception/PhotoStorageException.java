package com.example.dogs.exception;

public class PhotoStorageException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public PhotoStorageException(String message, Throwable cause) {
    super(message, cause);
  }
}