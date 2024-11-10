package com.mkhabibullin.presentation.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(description = "User information response")
public record UserResponseDTO(
  @Schema(description = "Unique identifier of the user")
  Long id,
  
  @Schema(
    description = "User's email address",
    example = "john.doe@example.com"
  )
  String email,
  
  @Schema(
    description = "User's display name",
    example = "John Doe"
  )
  String name,
  
  @Schema(
    description = "Whether the user has administrator privileges",
    example = "false"
  )
  boolean isAdmin,
  
  @Schema(
    description = "Whether the user account is blocked",
    example = "false"
  )
  boolean isBlocked
) {
}