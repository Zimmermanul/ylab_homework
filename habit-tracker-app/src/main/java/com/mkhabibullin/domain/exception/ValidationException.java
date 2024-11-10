package com.mkhabibullin.domain.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Exception thrown when data validation fails.
 * Maps to HTTP 400 Bad Request status code.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
@Schema(description = "Exception thrown when request validation fails")
public class ValidationException extends RuntimeException {
  
  private static final long serialVersionUID = 1L;
  
  @Schema(description = "List of validation error messages")
  private final List<ValidationError> validationErrors;
  
  /**
   * Record representing a single validation error.
   */
  @Schema(description = "Details of a single validation error")
  public record ValidationError(
    @Schema(description = "Field that failed validation", example = "email")
    String field,
    
    @Schema(description = "Error message", example = "must be a valid email address")
    String message,
    
    @Schema(description = "Invalid value that caused the error", example = "invalid-email")
    String rejectedValue
  ) {
  }
  
  /**
   * Constructs a new validation exception with the specified detail message.
   *
   * @param message the detail message explaining the validation failure
   */
  public ValidationException(String message) {
    super(message);
    this.validationErrors = Collections.singletonList(
      new ValidationError(null, message, null)
    );
  }
  
  /**
   * Constructs a new validation exception with a list of validation errors.
   *
   * @param validationErrors list of validation error messages
   */
  public ValidationException(List<ValidationError> validationErrors) {
    super(formatErrorMessage(validationErrors));
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
    this.validationErrors = Collections.singletonList(
      new ValidationError(null, message, null)
    );
  }
  
  /**
   * Returns the list of validation errors associated with this exception.
   *
   * @return an unmodifiable list of validation error details
   */
  public List<ValidationError> getValidationErrors() {
    return Collections.unmodifiableList(validationErrors);
  }
  
  /**
   * Creates a validation exception for a single field error.
   *
   * @param field         the name of the field that failed validation
   * @param message       the error message
   * @param rejectedValue the invalid value
   * @return a new ValidationException
   */
  public static ValidationException forField(String field, String message, Object rejectedValue) {
    return new ValidationException(Collections.singletonList(
      new ValidationError(field, message, String.valueOf(rejectedValue))
    ));
  }
  
  /**
   * Creates a validation exception for multiple field errors.
   *
   * @param errors map of field names to error messages
   * @return a new ValidationException
   */
  public static ValidationException forFields(Map<String, String> errors) {
    List<ValidationError> validationErrors = errors.entrySet().stream()
      .map(entry -> new ValidationError(entry.getKey(), entry.getValue(), null))
      .toList();
    return new ValidationException(validationErrors);
  }
  
  private static String formatErrorMessage(List<ValidationError> errors) {
    return errors.stream()
      .map(error -> error.field() != null ?
        String.format("%s: %s", error.field(), error.message()) :
        error.message())
      .collect(Collectors.joining("; "));
  }
}