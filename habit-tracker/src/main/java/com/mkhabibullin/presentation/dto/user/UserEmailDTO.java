package com.mkhabibullin.presentation.dto.user;

/**
 * Data Transfer Object containing only user email information.
 * Used in scenarios where only the email address is needed.
 *
 * @param email The email address associated with the user account
 */
public record UserEmailDTO(
  String email
) {
}
