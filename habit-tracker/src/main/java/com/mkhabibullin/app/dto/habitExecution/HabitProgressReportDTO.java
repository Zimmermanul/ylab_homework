package com.mkhabibullin.app.dto.habitExecution;

import java.util.List;

/**
 * Data Transfer Object containing a progress report for a habit.
 * Provides analysis and recommendations based on habit execution history.
 *
 * @param report         Textual summary of the habit progress
 * @param improvingTrend Flag indicating if the habit completion rate is improving over time
 * @param longestStreak  The longest consecutive streak of completed executions
 * @param suggestions    List of actionable recommendations for improving habit adherence
 */
public record HabitProgressReportDTO(
  String report,
  boolean improvingTrend,
  int longestStreak,
  List<String> suggestions
) {
}
