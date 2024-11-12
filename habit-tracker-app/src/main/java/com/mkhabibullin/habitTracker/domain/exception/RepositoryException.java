package com.mkhabibullin.habitTracker.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Base exception for repository-related errors.
 * Parent class for more specific repository exceptions.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class RepositoryException extends RuntimeException {
  public RepositoryException(String message) {
    super(message);
  }
  
  public RepositoryException(String message, Throwable cause) {
    super(message, cause);
  }
}