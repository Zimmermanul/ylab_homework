package com.mkhabibullin.presentation.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object for updating a user's name.
 * Used when a user wants to change their display name.
 *
 * @param newName The new name to be associated with the user account
 */
@Schema(description = "Name update request")
public record UpdateNameDTO(
  @Schema(
    description = "New display name",
    example = "John Smith",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String newName
) {
}