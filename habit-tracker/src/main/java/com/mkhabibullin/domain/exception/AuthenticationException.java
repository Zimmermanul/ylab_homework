package com.mkhabibullin.domain.exception;

/**
 * Exception thrown when authentication-related operations fail.
 */
public class AuthenticationException extends Exception {
  /**
   * Constructs a new authentication exception with the specified detail message.
   *
   * @param message the detail message explaining the reason for the authentication failure
   */
  public AuthenticationException(String message) {
    super(message);
  }
  
  /**
   * Constructs a new authentication exception with the specified detail message and cause.
   *
   * @param message the detail message explaining the reason for the authentication failure
   * @param cause   the underlying cause of the authentication failure
   */
  public AuthenticationException(String message, Throwable cause) {
    super(message, cause);
  }
}