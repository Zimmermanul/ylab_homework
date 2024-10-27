package com.mkhabibullin.app.dto.habitExecution;

import java.util.List;

public record HabitProgressReportDTO(
  String report,
  boolean improvingTrend,
  int longestStreak,
  List<String> suggestions
) {
}
