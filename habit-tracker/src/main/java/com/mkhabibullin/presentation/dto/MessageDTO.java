package com.mkhabibullin.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

/**
 * DTO for generic message responses.
 * Used to send simple string messages back to clients.
 * The message field is validated to ensure it's not null during construction.
 *
 * @param message The content of the message to be sent
 */
@Schema(description = "Success message response")
public record MessageDTO(
  @Schema(
    description = "Success message describing the operation result",
    example = "Operation completed successfully",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String message
) {
  public MessageDTO {
    Objects.requireNonNull(message, "Message cannot be null");
  }
}