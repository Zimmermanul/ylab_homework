package com.mkhabibullin.habitTracker.presentation.dto.system;

import io.swagger.v3.oas.annotations.media.Schema;


/**
 * Data Transfer Object containing detailed system uptime information.
 * Breaks down the total system uptime into days, hours, minutes, and seconds
 * for human-readable presentation.
 *
 * @param days    Number of complete days the system has been running
 * @param hours   Additional hours beyond complete days (0-23)
 * @param minutes Additional minutes beyond complete hours (0-59)
 * @param seconds Additional seconds beyond complete minutes (0-59)
 */
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
