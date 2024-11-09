package com.mkhabibullin.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested habit cannot be found in the system.
 * This exception occurs when attempting to retrieve, update, or delete
 * a habit that does not exist in the database.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class HabitNotFoundException extends RuntimeException {
  public HabitNotFoundException(String message) {
    super(message);
  }
  
  public HabitNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
