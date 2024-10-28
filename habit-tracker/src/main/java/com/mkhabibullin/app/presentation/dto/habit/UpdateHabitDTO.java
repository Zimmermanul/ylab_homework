package com.mkhabibullin.app.presentation.dto.habit;

import com.mkhabibullin.app.domain.model.Habit;

/**
 * Data Transfer Object for updating an existing habit.
 * This record contains the modifiable fields of a habit that can be updated.
 *
 * @param name        The new name for the habit
 * @param description The new description for the habit
 * @param frequency   The new frequency for the habit (e.g., DAILY, WEEKLY)
 */
public record UpdateHabitDTO(
  String name,
  String description,
  Habit.Frequency frequency
) {
}
