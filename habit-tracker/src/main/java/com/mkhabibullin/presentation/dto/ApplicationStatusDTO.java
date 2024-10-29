package com.mkhabibullin.presentation.dto;

/**
 * DTO for application status information.
 * Provides essential information about the application's current state
 * and the authenticated user's context.
 *
 * @param isRunning Flag indicating whether the application is currently running
 * @param isAuthenticated Flag indicating whether there is an authenticated user session
 * @param isAdmin Flag indicating whether the current user has administrative privileges
 * @param userEmail Email address of the currently authenticated user, or null if not authenticated
 */
public record ApplicationStatusDTO(
  boolean isRunning,
  boolean isAuthenticated,
  boolean isAdmin,
  String userEmail
) {
}