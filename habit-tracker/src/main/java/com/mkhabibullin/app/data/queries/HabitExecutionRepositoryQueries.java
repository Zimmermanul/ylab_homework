package com.mkhabibullin.app.data.queries;

/**
 * Contains SQL query constants used by the HabitExecutionDbRepository.
 * This class provides centralized storage for all SQL queries related to habit execution operations.
 * It cannot be instantiated as it only serves as a container for static constants.
 */
public final class HabitExecutionRepositoryQueries {
  
  private HabitExecutionRepositoryQueries() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  /**
   * SQL query for saving a new habit execution record.
   * This query inserts a new record into the habit_executions table and returns the generated ID.
   * Required parameters:
   * 1. habit_id (Long)
   * 2. date (Date)
   * 3. completed (Boolean)
   */
  public static final String SAVE_EXECUTION =
    "INSERT INTO entity.habit_executions (habit_id, date, completed) " +
    "VALUES (?, ?, ?) RETURNING id";
  
  /**
   * SQL query for retrieving all executions for a specific habit.
   * This query selects all execution records for a given habit ID, ordered by date.
   * Required parameters:
   * 1. habit_id (Long)
   */
  public static final String GET_BY_HABIT_ID =
    "SELECT * FROM entity.habit_executions WHERE habit_id = ? ORDER BY date";
  
  /**
   * SQL query for updating an existing habit execution record.
   * This query updates the date and completed status of an execution record.
   * Required parameters:
   * 1. date (Date)
   * 2. completed (Boolean)
   * 3. id (Long)
   */
  public static final String UPDATE_EXECUTION =
    "UPDATE entity.habit_executions SET date = ?, completed = ? WHERE id = ?";
  
  /**
   * SQL query for deleting a habit execution record.
   * This query removes an execution record from the database based on its ID.
   * Required parameters:
   * 1. id (Long)
   */
  public static final String DELETE_EXECUTION =
    "DELETE FROM entity.habit_executions WHERE id = ?";
  
  /**
   * SQL query for retrieving a specific habit execution by its ID.
   * This query selects a single execution record matching the provided ID.
   * Required parameters:
   * 1. id (Long)
   */
  public static final String GET_BY_ID =
    "SELECT * FROM entity.habit_executions WHERE id = ?";
  
  /**
   * SQL query for retrieving executions within a date range for a specific habit.
   * This query selects all execution records for a given habit ID between two dates.
   * Required parameters:
   * 1. habit_id (Long)
   * 2. start_date (Date)
   * 3. end_date (Date)
   */
  public static final String GET_BY_HABIT_AND_DATE_RANGE =
    "SELECT * FROM entity.habit_executions " +
    "WHERE habit_id = ? AND date BETWEEN ? AND ? ORDER BY date";
}