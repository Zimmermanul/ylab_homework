package com.mkhabibullin.app.dto.user;

/**
 * Data Transfer Object for user registration.
 * Contains the required information to create a new user account.
 *
 * @param email    The email address for the new user account
 * @param password The password for the new user account in plain text format
 * @param name     The user's full name or display name
 */
public record RegisterUserDTO(
  String email,
  String password,
  String name
) {
}
