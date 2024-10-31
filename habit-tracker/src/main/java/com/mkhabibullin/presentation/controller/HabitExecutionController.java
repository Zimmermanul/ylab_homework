package com.mkhabibullin.presentation.controller;

import com.mkhabibullin.application.service.HabitExecutionService;
import com.mkhabibullin.domain.model.Habit;
import com.mkhabibullin.domain.model.HabitExecution;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller class for managing habit executions in a habit tracking application.
 * This class acts as an intermediary between the user interface and the business logic,
 * delegating operations to the HabitExecutionService.
 */
public class HabitExecutionController {
  
  /**
   * The service responsible for habit execution-related business logic.
   */
  private HabitExecutionService executionService;
  
  /**
   * Constructs a new HabitExecutionController with the specified HabitExecutionService.
   *
   * @param executionService the service to handle habit execution-related operations
   */
  public HabitExecutionController(HabitExecutionService executionService) {
    this.executionService = executionService;
  }
  
  /**
   * Marks a habit as executed (completed or not) on a specific date.
   *
   * @param habitId   the unique identifier of the habit
   * @param date      the date of execution
   * @param completed whether the habit was completed or not
   * @throws IOException if there's an error during the marking process
   */
  public void markHabitExecution(Long habitId, LocalDate date, boolean completed) throws IOException {
    executionService.markHabitExecution(habitId, date, completed);
  }
  
  /**
   * Retrieves the execution history for a specific habit.
   *
   * @param habitId the unique identifier of the habit
   * @return a list of HabitExecution objects representing the execution history
   * @throws IOException if there's an error during the retrieval process
   */
  public List<HabitExecution> getHabitExecutionHistory(Long habitId) {
    return executionService.getHabitExecutionHistory(habitId);
  }
  
  /**
   * Retrieves the current streak (consecutive days of completion) for a habit.
   *
   * @param habitId the unique identifier of the habit
   * @return the current streak as an integer
   * @throws IOException if there's an error during the retrieval process
   */
  public int getCurrentStreak(Long habitId) {
    return executionService.getCurrentStreak(habitId);
  }
  
  /**
   * Calculates the success percentage of a habit within a specified date range.
   *
   * @param habitId   the unique identifier of the habit
   * @param startDate the start date of the range
   * @param endDate   the end date of the range
   * @return the success percentage as a double (0.0 to 100.0)
   * @throws IOException if there's an error during the calculation process
   */
  public double getSuccessPercentage(Long habitId, LocalDate startDate, LocalDate endDate) {
    return executionService.getSuccessPercentage(habitId, startDate, endDate);
  }
  
  /**
   * Generates a progress report for a habit within a specified date range.
   *
   * @param habitId   the unique identifier of the habit
   * @param startDate the start date of the range
   * @param endDate   the end date of the range
   * @return a string containing the progress report
   * @throws IOException if there's an error during the report generation process
   */
  public String generateProgressReport(Long habitId, LocalDate startDate, LocalDate endDate) {
    return executionService.generateProgressReport(habitId, startDate, endDate);
  }
  
  
  /**
   * Determines if there's an improving trend in habit execution.
   *
   * @param history A list of HabitExecution objects representing the execution history
   * @return true if there's an improving trend, false otherwise
   */
  public boolean isImprovingTrend(List<HabitExecution> history) {
    return executionService.isImprovingTrend(history);
  }
  
  
  /**
   * Calculates the longest streak of consecutive completed executions in the habit history.
   *
   * @param history A list of HabitExecution objects representing the execution history
   * @return The length of the longest streak of consecutive completed executions
   */
  public int calculateLongestStreak(List<HabitExecution> history) {
    return executionService.calculateLongestStreak(history);
  }
  
  /**
   * Generates personalized suggestions for improving a habit based on its execution history.
   *
   * @param habit   The Habit object for which suggestions are being generated
   * @param history A list of HabitExecution objects representing the execution history
   * @return A list of String suggestions for improving the habit
   */
  public List<String> generateSuggestions(Habit habit, List<HabitExecution> history) {
    return executionService.generateSuggestions(habit, history);
  }
}