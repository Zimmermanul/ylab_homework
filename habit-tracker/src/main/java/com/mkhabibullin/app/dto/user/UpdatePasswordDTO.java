package com.mkhabibullin.app.dto.user;

public record UpdatePasswordDTO(
  String oldPassword,
  String newPassword
) {
}
