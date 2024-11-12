package com.mkhabibullin.habitTracker.presentation.dto.habitExecution;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
/**
 * Data Transfer Object for creating a habit execution.
 * Contains validation constraints to ensure required fields are provided.
 *
 * @param date      The date for which the habit execution is being recorded. Must not be null.
 * @param completed Indicates whether the habit was completed. Must not be null.
 */
@Schema(description = "Request to record a habit execution")
public record HabitExecutionRequestDTO(
  @Schema(description = "Date of the habit execution", example = "2024-03-15")
  @NotNull(message = "Date is required")
  LocalDate date,
  
  @Schema(description = "Whether the habit was completed successfully", example = "true")
  @NotNull(message = "Completion status is required")
  Boolean completed
) {
}
