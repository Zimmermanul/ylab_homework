package com.mkhabibullin.app.dto.user;

/**
 * Data Transfer Object for updating multiple user fields simultaneously.
 * Allows updating both email and name in a single request.
 *
 * @param email The new email address for the user
 * @param name  The new name for the user
 */
public record UpdateUserDTO(
  String email,
  String name
) {
}
