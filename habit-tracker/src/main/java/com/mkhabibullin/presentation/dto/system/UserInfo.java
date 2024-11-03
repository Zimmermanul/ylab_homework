package com.mkhabibullin.presentation.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User information")
public record UserInfo(
  @Schema(description = "User ID")
  Long id,
  @Schema(description = "User email")
  String email,
  @Schema(description = "User name")
  String name,
  @Schema(description = "Admin status")
  boolean isAdmin
) {
}
