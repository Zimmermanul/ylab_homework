package com.mkhabibullin.domain.exception;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Exception thrown when data validation fails.
 */
public class ValidationException extends Exception {
  private final List<String> validationErrors;
  
  /**
   * Constructs a new validation exception with the specified detail message.
   *
   * @param message the detail message explaining the validation failure
   */
  public ValidationException(String message) {
    super(message);
    this.validationErrors = Collections.singletonList(message);
  }
  
  /**
   * Constructs a new validation exception with a list of validation errors.
   *
   * @param validationErrors list of validation error messages
   */
  public ValidationException(List<String> validationErrors) {
    super(String.join("; ", validationErrors));
    this.validationErrors = new ArrayList<>(validationErrors);
  }
  
  /**
   * Constructs a new validation exception with the specified detail message and cause.
   *
   * @param message the detail message explaining the validation failure
   * @param cause   the underlying cause of the validation failure
   */
  public ValidationException(String message, Throwable cause) {
    super(message, cause);
    this.validationErrors = Collections.singletonList(message);
  }
  
  /**
   * Returns the list of validation errors associated with this exception.
   *
   * @return an unmodifiable list of validation error messages
   */
  public List<String> getValidationErrors() {
    return Collections.unmodifiableList(validationErrors);
  }
}