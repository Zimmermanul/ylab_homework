package com.mkhabibullin.presentation.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "System uptime information")
public record UptimeInfo(
  @Schema(description = "Days up")
  long days,
  @Schema(description = "Hours up")
  long hours,
  @Schema(description = "Minutes up")
  long minutes,
  @Schema(description = "Seconds up")
  long seconds
) {
}
