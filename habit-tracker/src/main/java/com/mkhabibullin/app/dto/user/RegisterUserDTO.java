package com.mkhabibullin.app.dto.user;

public record RegisterUserDTO(
  String email,
  String password,
  String name
) {
}
