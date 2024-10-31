package com.mkhabibullin.presentation.dto.habit;

import java.time.LocalDate;

/**
 * Data Transfer Object for filtering habits in queries.
 * This record contains filter criteria for habit searches.
 *
 * @param filterDate The date to filter habits by
 * @param active     Filter flag for habit status - if true, returns only active habits;
 *                   if false, returns only inactive habits;
 *                   if null, returns all habits
 */
public record HabitFilterDTO(
  LocalDate filterDate,
  Boolean active
) {
}
