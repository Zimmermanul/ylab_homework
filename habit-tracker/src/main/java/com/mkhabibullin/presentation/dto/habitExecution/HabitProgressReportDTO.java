package com.mkhabibullin.presentation.dto.habitExecution;

import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(description = "Progress report for a habit")
public record HabitProgressReportDTO(
  @Schema(description = "Detailed progress report text")
  String report,
  
  @Schema(description = "Whether the habit shows an improving trend")
  boolean improvingTrend,
  
  @Schema(description = "Longest streak achieved for this habit")
  int longestStreak,
  
  @Schema(description = "List of suggestions for improvement")
  List<String> suggestions
) {
}
