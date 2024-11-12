package com.mkhabibullin.habitTracker.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource cannot be found.
 * This exception is used to indicate a 404 Not Found status in REST endpoints.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
  
  /**
   * Constructs a new resource not found exception with the specified message.
   *
   * @param message the detail message
   */
  public ResourceNotFoundException(String message) {
    super(message);
  }
  
  /**
   * Constructs a new resource not found exception with the specified message and cause.
   *
   * @param message the detail message
   * @param cause   the cause of the exception
   */
  public ResourceNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
  
  /**
   * Constructs a new resource not found exception for a specific resource type and ID.
   *
   * @param resourceName the type of resource that wasn't found (e.g., "Habit", "User")
   * @param id           the ID of the resource that wasn't found
   * @return a new ResourceNotFoundException with a formatted message
   */
  public static ResourceNotFoundException forResourceWithId(String resourceName, Object id) {
    return new ResourceNotFoundException(String.format("%s not found with id: %s", resourceName, id));
  }
}