package com.mkhabibullin.presentation.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object for user login request.
 * Contains the necessary credentials for user authentication.
 *
 * @param email    The user's email address used for authentication
 * @param password The user's password in plain text format
 */
@Schema(description = "User login request")
public record LoginDTO(
  @Schema(
    description = "User's email address",
    example = "john.doe@example.com",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String email,
  
  @Schema(
    description = "User's password",
    example = "password123",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String password
) {
}