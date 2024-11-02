package com.mkhabibullin.domain.exception;

public class EntityNotFoundException extends RuntimeException {
  
  public EntityNotFoundException(String message) {
    super(message);
  }
  
  public EntityNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public EntityNotFoundException(String entityName, Object identifier) {
    super(String.format("%s not found with identifier: %s", entityName, identifier));
  }
}