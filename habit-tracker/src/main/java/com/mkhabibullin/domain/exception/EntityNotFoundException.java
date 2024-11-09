package com.mkhabibullin.domain.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an entity cannot be found in the system.
 * This runtime exception is used when attempting to retrieve
 * or manipulate an entity that does not exist in the database.
 * Maps to HTTP 404 Not Found status code.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
@Schema(description = "Exception thrown when an entity cannot be found")
public class EntityNotFoundException extends RuntimeException {
  
  private static final long serialVersionUID = 1L;
  
  private final String entityName;
  private final String identifier;
  
  /**
   * Constructs a new EntityNotFoundException with the specified message.
   *
   * @param message the detail message describing the error
   */
  public EntityNotFoundException(String message) {
    super(message);
    this.entityName = null;
    this.identifier = null;
  }
  
  /**
   * Constructs a new EntityNotFoundException with the specified message and cause.
   *
   * @param message the detail message describing the error
   * @param cause   the cause of this exception
   */
  public EntityNotFoundException(String message, Throwable cause) {
    super(message, cause);
    this.entityName = null;
    this.identifier = null;
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
    this.entityName = entityName;
    this.identifier = String.valueOf(identifier);
  }
  
  /**
   * Creates an exception for an entity not found by ID.
   *
   * @param entityName the name of the entity type
   * @param id         the ID that was searched for
   * @return a new EntityNotFoundException
   */
  public static EntityNotFoundException forId(String entityName, Object id) {
    return new EntityNotFoundException(entityName, id);
  }
  
  /**
   * Creates an exception for an entity not found by a specific field.
   *
   * @param entityName the name of the entity type
   * @param fieldName  the name of the field searched by
   * @param fieldValue the value that was searched for
   * @return a new EntityNotFoundException
   */
  public static EntityNotFoundException forField(String entityName, String fieldName, Object fieldValue) {
    return new EntityNotFoundException(
      String.format("%s not found with %s: %s", entityName, fieldName, fieldValue)
    );
  }
  
  public String getEntityName() {
    return entityName;
  }
  
  public String getIdentifier() {
    return identifier;
  }
}