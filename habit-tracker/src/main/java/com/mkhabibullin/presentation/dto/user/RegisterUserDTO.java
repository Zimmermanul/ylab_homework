package com.mkhabibullin.presentation.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object for user registration.
 * Contains the required information to create a new user account.
 *
 * @param email    The email address for the new user account
 * @param password The password for the new user account in plain text format
 * @param name     The user's full name or display name
 */
@Schema(description = "User registration request")
public record RegisterUserDTO(
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
  String password,
  
  @Schema(
    description = "User's display name",
    example = "John Doe",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String name
) {
}