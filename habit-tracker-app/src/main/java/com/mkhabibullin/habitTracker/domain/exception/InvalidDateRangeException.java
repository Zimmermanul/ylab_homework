package com.mkhabibullin.habitTracker.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when date-related validation fails.
 * This exception is used for various date validation scenarios such as:
 * <ul>
 *   <li>Missing required dates</li>
 *   <li>Invalid date ranges</li>
 *   <li>Dates outside allowed bounds</li>
 * </ul>
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidDateRangeException extends RuntimeException {
  public InvalidDateRangeException(String message) {
    super(message);
  }
  
  public InvalidDateRangeException(String message, Throwable cause) {
    super(message, cause);
  }
}