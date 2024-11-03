package com.mkhabibullin.domain.exception;

/**
 * Exception thrown when an entity cannot be found in the system.
 * This runtime exception is used when attempting to retrieve
 * or manipulate an entity that does not exist in the database.
 */
public class EntityNotFoundException extends RuntimeException {
  
  /**
   * Constructs a new EntityNotFoundException with the specified message.
   *
   * @param message the detail message describing the error
   */
  public EntityNotFoundException(String message) {
    super(message);
  }
  
  /**
   * Constructs a new EntityNotFoundException with the specified message and cause.
   *
   * @param message the detail message describing the error
   * @param cause   the cause of this exception
   */
  public EntityNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
  
  /**
   * Constructs a new EntityNotFoundException with a formatted message containing
   * the entity name and identifier that could not be found.
   *
   * @param entityName the name of the entity type that could not be found
   * @param identifier the identifier value that was searched for
   */
  public EntityNotFoundException(String entityName, Object identifier) {
    super(String.format("%s not found with identifier: %s", entityName, identifier));
  }
}