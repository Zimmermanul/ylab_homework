package com.mkhabibullin.audit.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

/**
 * DTO for error message responses.
 * Used to communicate error information to clients in a structured format.
 * The message field is validated to ensure it's not null during construction.
 *
 * @param message   Descriptive error message explaining what went wrong
 * @param timestamp Unix timestamp when the error occurred
 */

@Schema(description = "Error response containing details about the error")
public record ErrorDTO(
  @Schema(
    description = "Error message describing what went wrong",
    example = "Resource not found",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String message,
  
  @Schema(
    description = "Unix timestamp when the error occurred",
    example = "1709645400000",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  long timestamp
) {
  public ErrorDTO {
    Objects.requireNonNull(message, "Message cannot be null");
  }
}