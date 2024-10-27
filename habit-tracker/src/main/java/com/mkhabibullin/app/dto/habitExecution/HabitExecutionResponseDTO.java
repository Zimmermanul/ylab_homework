package com.mkhabibullin.app.dto.habitExecution;

import java.time.LocalDate;

public record HabitExecutionResponseDTO(
  Long id,
  Long habitId,
  LocalDate date,
  boolean completed
) {
}