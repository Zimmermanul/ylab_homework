package com.mkhabibullin.habitTracker.infrastructure.persistence.repository;

import com.mkhabibullin.habitTracker.domain.model.HabitExecution;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for HabitExecution entities.
 * Defines operations for tracking habit completions and retrieving execution history.
 */
public interface HabitExecutionRepository {
  
  /**
   * Saves a new habit execution record.
   *
   * @param execution The habit execution to save
   */
  void save(HabitExecution execution);
  
  /**
   * Retrieves all execution records for a specific habit.
   *
   * @param habitId The ID of the habit
   * @return List of executions for the habit
   */
  List<HabitExecution> getByHabitId(Long habitId);
  
  /**
   * Updates an existing habit execution record.
   *
   * @param execution The execution to update
   */
  void update(HabitExecution execution);
  
  /**
   * Deletes a habit execution record.
   *
   * @param executionId The ID of the execution to delete
   */
  void delete(Long executionId);
  
  /**
   * Retrieves a specific habit execution by its ID.
   *
   * @param id The ID of the execution
   * @return The habit execution if found, null otherwise
   */
  HabitExecution getById(Long id);
  
  /**
   * Retrieves executions for a habit within a date range.
   *
   * @param habitId   The ID of the habit
   * @param startDate The start date of the range
   * @param endDate   The end date of the range
   * @return List of executions within the range
   */
  List<HabitExecution> getByHabitAndDateRange(Long habitId, LocalDate startDate, LocalDate endDate);
}