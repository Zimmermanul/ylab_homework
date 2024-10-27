package com.mkhabibullin.app.dto;

/**
 * DTO for application status information
 */
public record ApplicationStatusDTO(
  boolean isRunning,
  boolean isAuthenticated,
  boolean isAdmin,
  String userEmail
) {
}