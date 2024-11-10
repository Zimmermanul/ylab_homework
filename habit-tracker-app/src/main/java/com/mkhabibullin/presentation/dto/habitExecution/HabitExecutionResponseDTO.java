package com.mkhabibullin.presentation.dto.habitExecution;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
/**
 * Data Transfer Object representing a habit execution response.
 * Contains information about a specific instance of habit execution.
 *
 * @param id        Unique identifier for the habit execution
 * @param habitId   Reference to the habit this execution belongs to
 * @param date      The date when this habit execution was recorded
 * @param completed Flag indicating whether the habit was completed on the specified date
 */
@Schema(description = "Response containing habit execution details")
public record HabitExecutionResponseDTO(
  @Schema(description = "Unique identifier of the execution")
  Long id,
  
  @Schema(description = "ID of the associated habit")
  Long habitId,
  
  @Schema(description = "Date of the execution", example = "2024-03-15")
  LocalDate date,
  
  @Schema(description = "Whether the habit was completed")
  boolean completed
) {
}
