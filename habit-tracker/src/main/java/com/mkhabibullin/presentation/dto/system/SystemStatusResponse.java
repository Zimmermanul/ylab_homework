package com.mkhabibullin.presentation.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

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

