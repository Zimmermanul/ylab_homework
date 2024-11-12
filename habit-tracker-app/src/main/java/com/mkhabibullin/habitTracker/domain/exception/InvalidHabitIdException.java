package com.mkhabibullin.habitTracker.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the provided habit ID format is invalid.
 * This typically occurs when attempting to parse a non-numeric
 * string as a habit ID.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidHabitIdException extends RuntimeException {
  public InvalidHabitIdException(String message) {
    super(message);
  }
  
  public InvalidHabitIdException(String message, Throwable cause) {
    super(message, cause);
  }
}