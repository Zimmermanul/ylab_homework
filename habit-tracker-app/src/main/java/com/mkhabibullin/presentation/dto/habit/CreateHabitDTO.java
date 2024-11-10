package com.mkhabibullin.presentation.dto.habit;

import com.mkhabibullin.domain.model.Habit;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Data Transfer Object for creating a new habit.
 * This record encapsulates the essential information needed to create a habit.
 *
 * @param name        The name of the habit to be created
 * @param description Description of the habit
 * @param frequency   The frequency at which the habit should be performed (e.g., DAILY, WEEKLY)
 */
@Schema(description = "Request to create a new habit")
public record CreateHabitDTO(
  @Schema(description = "Name of the habit", example = "Morning Exercise")
  String name,
  
  @Schema(description = "Detailed description of the habit", example = "30 minutes of cardio exercise every morning")
  String description,
  
  @Schema(description = "Frequency of the habit", example = "DAILY")
  Habit.Frequency frequency
) {
}

