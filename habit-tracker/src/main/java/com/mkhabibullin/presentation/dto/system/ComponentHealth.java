package com.mkhabibullin.presentation.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

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
