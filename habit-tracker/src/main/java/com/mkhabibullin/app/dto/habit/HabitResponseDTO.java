package com.mkhabibullin.app.dto.habit;

import com.mkhabibullin.app.model.Habit;

import java.time.LocalDate;

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
