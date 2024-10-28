package com.mkhabibullin.app.dto.habit;

import com.mkhabibullin.app.model.Habit;

/**
 * Data Transfer Object for creating a new habit.
 * This record encapsulates the essential information needed to create a habit.
 *
 * @param name        The name of the habit to be created
 * @param description Description of the habit
 * @param frequency   The frequency at which the habit should be performed (e.g., DAILY, WEEKLY)
 */
public record CreateHabitDTO(
  String name,
  String description,
  Habit.Frequency frequency
) {
}