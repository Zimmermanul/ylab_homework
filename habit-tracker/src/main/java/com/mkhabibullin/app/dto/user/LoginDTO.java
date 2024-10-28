package com.mkhabibullin.app.dto.user;

/**
 * Data Transfer Object for user login request.
 * Contains the necessary credentials for user authentication.
 *
 * @param email    The user's email address used for authentication
 * @param password The user's password in plain text format
 */
public record LoginDTO(
  String email,
  String password
) {
}

