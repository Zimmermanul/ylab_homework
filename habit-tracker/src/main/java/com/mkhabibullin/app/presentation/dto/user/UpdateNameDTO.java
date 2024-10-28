package com.mkhabibullin.app.presentation.dto.user;

/**
 * Data Transfer Object for updating a user's name.
 * Used when a user wants to change their display name.
 *
 * @param newName The new name to be associated with the user account
 */
public record UpdateNameDTO(
  String newName
) {
}
