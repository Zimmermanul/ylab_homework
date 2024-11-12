package com.mkhabibullin.habitTracker.presentation.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object for password updates.
 * Contains both the old and new passwords for verification purposes.
 *
 * @param oldPassword The user's current password for verification
 * @param newPassword The new password to be set for the account
 */
@Schema(description = "Password update request")
public record UpdatePasswordDTO(
  @Schema(
    description = "Current password",
    example = "oldPassword123",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String oldPassword,
  
  @Schema(
    description = "New password",
    example = "newPassword123",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String newPassword
) {
}