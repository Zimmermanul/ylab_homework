package com.mkhabibullin.habitTracker.presentation.dto.habit;

import com.mkhabibullin.habitTracker.domain.model.Habit;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
/**
 * Data Transfer Object representing a habit's complete information in responses.
 * This record contains all relevant details about a habit, including its metadata.
 *
 * @param id           Unique identifier for the habit
 * @param userId       Identifier of the user who owns this habit
 * @param name         The name of the habit
 * @param description  Detailed description of the habit
 * @param frequency    The frequency at which the habit should be performed (e.g., DAILY, WEEKLY)
 * @param creationDate The date when the habit was created
 * @param active       Flag indicating whether the habit is currently active
 */
@Schema(description = "Habit information response")
public record HabitResponseDTO(
  @Schema(description = "Unique identifier of the habit")
  Long id,
  
  @Schema(description = "ID of the user who owns this habit")
  Long userId,
  
  @Schema(description = "Name of the habit", example = "Morning Exercise")
  String name,
  
  @Schema(description = "Detailed description of the habit", example = "30 minutes of cardio exercise every morning")
  String description,
  
  @Schema(description = "Frequency of the habit", example = "DAILY")
  Habit.Frequency frequency,
  
  @Schema(description = "Date when the habit was created")
  LocalDate creationDate,
  
  @Schema(description = "Whether the habit is currently active")
  boolean active
) {
}
