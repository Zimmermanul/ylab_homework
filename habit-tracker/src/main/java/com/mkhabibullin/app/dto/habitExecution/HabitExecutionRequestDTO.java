package com.mkhabibullin.app.dto.habitExecution;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * Data Transfer Object for creating a habit execution.
 * Contains validation constraints to ensure required fields are provided.
 *
 * @param date      The date for which the habit execution is being recorded. Must not be null.
 * @param completed Indicates whether the habit was completed. Must not be null.
 */
public record HabitExecutionRequestDTO(
  @NotNull(message = "Date is required")
  LocalDate date,
  
  @NotNull(message = "Completion status is required")
  Boolean completed
) {
}
