package com.mkhabibullin.app.dto.habitExecution;

import java.time.DayOfWeek;
import java.util.Map;

public record HabitStatisticsDTO(
  int currentStreak,
  double successPercentage,
  long totalExecutions,
  long completedExecutions,
  long missedExecutions,
  Map<DayOfWeek, Long> completionsByDay
) {
}
