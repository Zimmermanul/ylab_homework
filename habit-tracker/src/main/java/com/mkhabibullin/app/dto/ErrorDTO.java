package com.mkhabibullin.app.dto;

import java.util.Objects;

public record ErrorDTO(String message, long timestamp) {
  public ErrorDTO {
    Objects.requireNonNull(message, "Message cannot be null");
  }
}