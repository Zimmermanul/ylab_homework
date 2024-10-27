package com.mkhabibullin.app.dto;

import java.util.Objects;

public record MessageDTO(String message) {
  public MessageDTO {
    Objects.requireNonNull(message, "Message cannot be null");
  }
}