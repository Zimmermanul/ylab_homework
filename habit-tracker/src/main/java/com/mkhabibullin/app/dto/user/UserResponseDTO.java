package com.mkhabibullin.app.dto.user;

public record UserResponseDTO(
  Long id,
  String email,
  String name,
  boolean isAdmin,
  boolean isBlocked
) {
}
