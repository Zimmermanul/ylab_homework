package com.mkhabibullin.presentation.dto.habitExecution;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

/**
 * Record representing a single habit execution entry
 */
@Schema(description = "Single execution record for a habit")
public record ExecutionHistoryDTO(
  @Schema(description = "Date of the execution", example = "2024-01-15")
  LocalDate date,
  
  @Schema(description = "Whether the habit was completed on this date", example = "true")
  boolean completed
) {
}
