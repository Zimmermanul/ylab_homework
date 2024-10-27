package com.mkhabibullin.app.dto.habitExecution;

import java.time.LocalDate;

public record DateRangeDTO(
  LocalDate startDate,
  LocalDate endDate
) {
}
