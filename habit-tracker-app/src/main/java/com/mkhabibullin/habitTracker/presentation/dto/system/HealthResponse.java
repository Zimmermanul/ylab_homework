package com.mkhabibullin.habitTracker.presentation.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Data Transfer Object representing the overall system health check response.
 * Aggregates health information from various system components and provides
 * a comprehensive view of system health.
 *
 * @param status     Overall system health status (e.g., "healthy", "unhealthy")
 * @param timestamp  Time when the health check was performed
 * @param components Detailed health information for each system component
 */
@Schema(description = "Health check response")
public record HealthResponse(
  @Schema(description = "Overall health status")
  String status,
  @Schema(description = "Current timestamp")
  LocalDateTime timestamp,
  @Schema(description = "Component health details")
  Map<String, ComponentHealth> components
) {
}
