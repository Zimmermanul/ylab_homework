package com.mkhabibullin.app.dto.habit;

import com.mkhabibullin.app.model.Habit;

public record UpdateHabitDTO(
  String name,
  String description,
  Habit.Frequency frequency
) {
}
