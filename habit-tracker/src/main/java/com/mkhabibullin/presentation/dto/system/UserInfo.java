package com.mkhabibullin.presentation.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object containing essential user information.
 * Provides key user details for display and authorization purposes
 * while excluding sensitive information.
 *
 * @param id      Unique identifier for the user
 * @param email   User's email address used for authentication
 * @param name    User's display name
 * @param isAdmin Flag indicating whether the user has administrative privileges
 */
@Schema(description = "User information")
public record UserInfo(
  @Schema(description = "User ID")
  Long id,
  @Schema(description = "User email")
  String email,
  @Schema(description = "User name")
  String name,
  @Schema(description = "Admin status")
  boolean isAdmin
) {
}
