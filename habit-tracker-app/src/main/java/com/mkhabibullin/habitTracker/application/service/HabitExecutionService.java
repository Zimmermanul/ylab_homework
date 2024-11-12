package com.mkhabibullin.habitTracker.application.service;

import com.mkhabibullin.habitTracker.domain.model.Habit;
import com.mkhabibullin.habitTracker.domain.model.HabitExecution;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Service interface for managing habit executions in a habit tracking application.
 * Provides functionality for tracking habit completions, analyzing performance,
 * and generating insights about habit execution patterns.
 */
@Validated
public interface HabitExecutionService {
  /**
   * Marks a habit as executed (completed or not) on a specific date.
   *
   * @param habitId   the ID of the habit
   * @param date      the date of execution
   * @param completed whether the habit was completed
   */
  @Transactional
  void markHabitExecution(@NotNull Long habitId, @NotNull LocalDate date, @NotNull boolean completed);
  
  /**
   * Retrieves the execution history for a specific habit.
   *
   * @param habitId the ID of the habit
   * @return a list of HabitExecution objects representing the execution history
   */
  List<HabitExecution> getAll(@NotNull Long habitId);
  
  /**
   * Calculates the current streak (consecutive days of completion) for a habit.
   *
   * @param habitId the ID of the habit
   * @return the current streak as an integer
   */
  int getCurrentStreak(@NotNull Long habitId);
  
  /**
   * Calculates the success percentage of a habit within a specified date range.
   *
   * @param habitId   the ID of the habit
   * @param startDate the start date of the range
   * @param endDate   the end date of the range
   * @return the success percentage as a double between 0.0 and 100.0
   */
  double getSuccessPercentage(@NotNull Long habitId, @NotNull LocalDate startDate, @NotNull LocalDate endDate);
  
  /**
   * Generates a detailed progress report for a habit within a specified date range.
   *
   * @param habitId   the ID of the habit
   * @param startDate the start date of the range
   * @param endDate   the end date of the range
   * @return a map of string containing the progress report
   */
  Map<String, String> generateProgressReport(@NotNull Long habitId, @NotNull LocalDate startDate, @NotNull LocalDate endDate);
  
  /**
   * Determines if there's an improving trend in habit execution.
   * This method compares the completion rate of the first half of the history
   * with the second half.
   *
   * @param history A list of HabitExecution objects representing the execution history
   * @return true if the second half shows improvement, false otherwise
   */
  boolean isImprovingTrend(@NotNull @Valid List<HabitExecution> history);
  
  /**
   * Calculates the longest streak of consecutive completed executions in the habit history.
   *
   * @param history A list of HabitExecution objects representing the execution history
   * @return The length of the longest streak of consecutive completed executions
   */
  int calculateLongestStreak(@NotNull @Valid List<HabitExecution> history);
  
  /**
   * Generates personalized suggestions for improving a habit based on its execution history.
   * This method takes into account factors such as completion rate, habit frequency,
   * habit description, and the duration since the habit was created.
   *
   * @param habit   The Habit object for which suggestions are being generated
   * @param history A list of HabitExecution objects representing the execution history
   * @return A list of String suggestions for improving the habit
   */
  List<String> generateSuggestions(@NotNull @Valid Habit habit, @NotNull @Valid List<HabitExecution> history);
}