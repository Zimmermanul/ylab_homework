package com.mkhabibullin.presentation.dto.habit;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(description = "Success message response")
public record MessageDTO(
  @Schema(description = "Success message", example = "Operation completed successfully")
  String message
) {
  public MessageDTO {
    Objects.requireNonNull(message, "Message cannot be null");
  }
}
