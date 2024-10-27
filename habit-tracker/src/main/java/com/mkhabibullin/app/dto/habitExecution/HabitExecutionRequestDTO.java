package com.mkhabibullin.app.dto.habitExecution;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record HabitExecutionRequestDTO(
  @NotNull(message = "Date is required")
  LocalDate date,
  
  @NotNull(message = "Completion status is required")
  Boolean completed
) {
}
