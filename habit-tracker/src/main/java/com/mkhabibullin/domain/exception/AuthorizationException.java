package com.mkhabibullin.domain.exception;

/**
 * Exception thrown when authorization-related operations fail.
 */
public class AuthorizationException extends Exception {
  /**
   * Constructs a new authorization exception with the specified detail message.
   *
   * @param message the detail message explaining the reason for the authorization failure
   */
  public AuthorizationException(String message) {
    super(message);
  }
  
  /**
   * Constructs a new authorization exception with the specified detail message and cause.
   *
   * @param message the detail message explaining the reason for the authorization failure
   * @param cause   the underlying cause of the authorization failure
   */
  public AuthorizationException(String message, Throwable cause) {
    super(message, cause);
  }
}