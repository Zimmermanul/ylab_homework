package com.mkhabibullin.app.presentation.dto;

import java.util.Objects;

/**
 * DTO for generic message responses.
 * Used to send simple string messages back to clients.
 * The message field is validated to ensure it's not null during construction.
 *
 * @param message The content of the message to be sent
 */
public record MessageDTO(String message) {
  public MessageDTO {
    Objects.requireNonNull(message, "Message cannot be null");
  }
}