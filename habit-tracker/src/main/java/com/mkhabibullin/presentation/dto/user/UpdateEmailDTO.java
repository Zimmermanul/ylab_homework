package com.mkhabibullin.presentation.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object for updating a user's email address.
 * Used when a user wants to change their email address.
 *
 * @param newEmail The new email address to be associated with the user account
 */
@Schema(description = "Email update request")
public record UpdateEmailDTO(
  @Schema(
    description = "New email address",
    example = "new.email@example.com",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String newEmail
) {
}