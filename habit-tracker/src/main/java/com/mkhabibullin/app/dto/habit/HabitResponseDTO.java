package com.mkhabibullin.app.dto.habit;

import com.mkhabibullin.app.model.Habit;

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
public record HabitResponseDTO(
  Long id,
  Long userId,
  String name,
  String description,
  Habit.Frequency frequency,
  LocalDate creationDate,
  boolean active
) {
}
