package com.mkhabibullin.app.dto.habitExecution;

import java.time.LocalDate;


/**
 * Data Transfer Object representing a date range for habit execution analytics.
 *
 * @param startDate The beginning date of the range (inclusive)
 * @param endDate   The ending date of the range (inclusive)
 */
public record DateRangeDTO(
  LocalDate startDate,
  LocalDate endDate
) {
}
