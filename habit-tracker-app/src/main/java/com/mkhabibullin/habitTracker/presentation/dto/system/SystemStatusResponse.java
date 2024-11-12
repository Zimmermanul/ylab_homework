package com.mkhabibullin.habitTracker.presentation.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing the complete system status.
 * Provides comprehensive information about the system's current state,
 * including runtime information and authenticated user details.
 *
 * @param status        Current operating status of the system
 * @param timestamp     Time when the status was generated
 * @param startupTime   Time when the system was started
 * @param uptime        Detailed breakdown of system uptime
 * @param user          Information about the currently authenticated user (null if not authenticated)
 * @param authenticated Flag indicating if there is an authenticated user session
 */
@Schema(description = "System status response")
public record SystemStatusResponse(
  @Schema(description = "Current system status")
  String status,
  @Schema(description = "Current timestamp")
  LocalDateTime timestamp,
  @Schema(description = "System startup time")
  LocalDateTime startupTime,
  @Schema(description = "System uptime details")
  UptimeInfo uptime,
  @Schema(description = "Current user information")
  UserInfo user,
  @Schema(description = "Authentication status")
  boolean authenticated
) {
}

