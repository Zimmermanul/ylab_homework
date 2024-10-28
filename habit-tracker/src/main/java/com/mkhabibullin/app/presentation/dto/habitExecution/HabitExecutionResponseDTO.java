package com.mkhabibullin.app.presentation.dto.habitExecution;

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
public record HabitExecutionResponseDTO(
  Long id,
  Long habitId,
  LocalDate date,
  boolean completed
) {
}