package com.mkhabibullin.presentation.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

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
