package com.mkhabibullin.presentation.dto.habitExecution;

import java.time.DayOfWeek;
import java.util.Map;

/**
 * Data Transfer Object containing statistical analysis of habit executions.
 * Provides various metrics and aggregated data about habit performance.
 *
 * @param currentStreak       The current consecutive streak of completed executions
 * @param successPercentage   The percentage of successful completions relative to total executions
 * @param totalExecutions     Total number of times the habit was scheduled
 * @param completedExecutions Number of times the habit was successfully completed
 * @param missedExecutions    Number of times the habit was not completed as scheduled
 * @param completionsByDay    Map showing completion counts grouped by day of the week
 */
public record HabitStatisticsDTO(
  int currentStreak,
  double successPercentage,
  long totalExecutions,
  long completedExecutions,
  long missedExecutions,
  Map<DayOfWeek, Long> completionsByDay
) {
}
