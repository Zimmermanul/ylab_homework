package com.mkhabibullin.habitTracker.presentation.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object containing only user email information.
 * Used in scenarios where only the email address is needed.
 *
 * @param email The email address associated with the user account
 */
@Schema(description = "User email request")
public record UserEmailDTO(
  @Schema(
    description = "User's email address",
    example = "user@example.com",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String email
) {
}