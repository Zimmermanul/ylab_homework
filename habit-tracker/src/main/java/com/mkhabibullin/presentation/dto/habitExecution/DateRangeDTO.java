package com.mkhabibullin.presentation.dto.habitExecution;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;


/**
 * Data Transfer Object representing a date range for habit execution analytics.
 *
 * @param startDate The beginning date of the range (inclusive)
 * @param endDate   The ending date of the range (inclusive)
 */
@Schema(description = "Date range for filtering habit executions")
public record DateRangeDTO(
  @Schema(description = "Start date of the range", example = "2024-01-01")
  LocalDate startDate,
  
  @Schema(description = "End date of the range", example = "2024-12-31")
  LocalDate endDate
) {
}

