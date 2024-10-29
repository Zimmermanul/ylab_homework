package com.mkhabibullin.presentation.dto;

import java.util.Objects;

/**
 * DTO for error message responses.
 * Used to communicate error information to clients in a structured format.
 * The message field is validated to ensure it's not null during construction.
 *
 * @param message   Descriptive error message explaining what went wrong
 * @param timestamp Unix timestamp when the error occurred
 */
public record ErrorDTO(String message, long timestamp) {
  public ErrorDTO {
    Objects.requireNonNull(message, "Message cannot be null");
  }
}