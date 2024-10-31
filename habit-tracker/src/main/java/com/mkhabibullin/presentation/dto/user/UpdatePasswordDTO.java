package com.mkhabibullin.presentation.dto.user;

/**
 * Data Transfer Object for password updates.
 * Contains both the old and new passwords for verification purposes.
 *
 * @param oldPassword The user's current password for verification
 * @param newPassword The new password to be set for the account
 */
public record UpdatePasswordDTO(
  String oldPassword,
  String newPassword
) {
}
