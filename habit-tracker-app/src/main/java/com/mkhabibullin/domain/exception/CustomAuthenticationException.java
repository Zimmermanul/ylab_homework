package com.mkhabibullin.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception thrown when authentication-related operations fail.
 * Maps to HTTP 401 Unauthorized status code.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class CustomAuthenticationException extends RuntimeException {
  
  private static final long serialVersionUID = 1L;
  
  /**
   * Constructs a new authentication exception with the specified detail message.
   *
   * @param message the detail message explaining the reason for the authentication failure
   */
  public CustomAuthenticationException(String message) {
    super(message);
  }
  
  /**
   * Constructs a new authentication exception with the specified detail message and cause.
   *
   * @param message the detail message explaining the reason for the authentication failure
   * @param cause   the underlying cause of the authentication failure
   */
  public CustomAuthenticationException(String message, Throwable cause) {
    super(message, cause);
  }
  
  /**
   * Creates an authentication exception for invalid credentials.
   *
   * @param username the username that failed authentication
   * @return a new CustomAuthenticationException with a formatted message
   */
  public static CustomAuthenticationException invalidCredentials(String username) {
    return new CustomAuthenticationException(String.format("Invalid credentials for user: %s", username));
  }
  
  /**
   * Creates an authentication exception for an expired token.
   *
   * @return a new CustomAuthenticationException with an appropriate message
   */
  public static CustomAuthenticationException tokenExpired() {
    return new CustomAuthenticationException("Authentication token has expired");
  }
  
  /**
   * Creates an authentication exception for a missing token.
   *
   * @return a new CustomAuthenticationException with an appropriate message
   */
  public static CustomAuthenticationException missingToken() {
    return new CustomAuthenticationException("Authentication token is missing");
  }
  
  /**
   * Creates an authentication exception for an invalid token.
   *
   * @return a new CustomAuthenticationException with an appropriate message
   */
  public static CustomAuthenticationException invalidToken() {
    return new CustomAuthenticationException("Authentication token is invalid");
  }
}