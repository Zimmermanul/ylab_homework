package com.mkhabibullin.presentation.dto.user;

/**
 * Data Transfer Object for updating a user's email address.
 * Used when a user wants to change their email address.
 *
 * @param newEmail The new email address to be associated with the user account
 */
public record UpdateEmailDTO(
  String newEmail
) {
}
