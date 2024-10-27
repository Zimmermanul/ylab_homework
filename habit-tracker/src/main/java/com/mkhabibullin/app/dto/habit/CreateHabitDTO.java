package com.mkhabibullin.app.dto.habit;

import com.mkhabibullin.app.model.Habit;

public record CreateHabitDTO(
  String name,
  String description,
  Habit.Frequency frequency
) {
}