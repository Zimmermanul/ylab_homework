package com.mkhabibullin.presentation.dto.habit;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(description = "Error response")
public record ErrorDTO(
  @Schema(description = "Error message describing what went wrong")
  String message,
  
  @Schema(description = "Timestamp when the error occurred")
  long timestamp
) {
  public ErrorDTO {
    Objects.requireNonNull(message, "Message cannot be null");
  }
}
