package com.mkhabibullin.habitTracker.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an email address format is invalid.
 * This exception is used during user creation or email updates
 * when the provided email doesn't match the required format pattern.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidEmailException extends RuntimeException {
  public InvalidEmailException(String message) {
    super(message);
  }
}