package com.mkhabibullin.presentation.dto.user;

/**
 * Data Transfer Object for user information responses.
 * Contains comprehensive user account information including status flags.
 *
 * @param id        Unique identifier for the user
 * @param email     The user's email address
 * @param name      The user's display name
 * @param isAdmin   Flag indicating whether the user has administrative privileges
 * @param isBlocked Flag indicating whether the user account is currently blocked
 */
public record UserResponseDTO(
  Long id,
  String email,
  String name,
  boolean isAdmin,
  boolean isBlocked
) {
}
