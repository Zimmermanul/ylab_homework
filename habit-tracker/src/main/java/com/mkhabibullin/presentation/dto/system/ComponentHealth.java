package com.mkhabibullin.presentation.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

/**
 * Data Transfer Object representing the health status of an individual system component.
 * Used to provide detailed health information for specific parts of the system
 * such as database connections, memory usage, or external services.
 *
 * @param status  Current status of the component (e.g., "up", "down")
 * @param error   Error message if component is unhealthy, null otherwise
 * @param details Additional component-specific metrics and information
 */
@Schema(description = "Component health information")
public record ComponentHealth(
  @Schema(description = "Component status")
  String status,
  @Schema(description = "Error message if any")
  String error,
  @Schema(description = "Additional component details")
  Map<String, Object> details
) {
}
