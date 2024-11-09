package com.mkhabibullin.presentation.dto.habitExecution;

import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(description = "Statistical analysis of habit execution")
public record HabitStatisticsDTO(
  @Schema(description = "Current streak of successful completions")
  int currentStreak,
  
  @Schema(description = "Overall success percentage", example = "85.5")
  double successPercentage,
  
  @Schema(description = "Total number of execution attempts")
  long totalExecutions,
  
  @Schema(description = "Number of successful completions")
  long completedExecutions,
  
  @Schema(description = "Number of missed executions")
  long missedExecutions,
  
  @Schema(description = "Completion count grouped by day of week")
  Map<DayOfWeek, Long> completionsByDay
) {
}
