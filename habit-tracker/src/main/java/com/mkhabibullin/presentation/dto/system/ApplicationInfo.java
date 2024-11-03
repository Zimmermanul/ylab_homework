package com.mkhabibullin.presentation.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Application information")
public record ApplicationInfo(
  @Schema(description = "Application name")
  String name,
  @Schema(description = "Application version")
  String version
) {
}
