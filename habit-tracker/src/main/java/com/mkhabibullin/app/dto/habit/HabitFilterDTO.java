package com.mkhabibullin.app.dto.habit;

import java.time.LocalDate;

public record HabitFilterDTO(
  LocalDate filterDate,
  Boolean active
) {
}
