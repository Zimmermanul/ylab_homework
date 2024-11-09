package com.mkhabibullin.infrastructure.persistence.queries;
/**
 * Contains JPQL query constants used by the HabitExecutionRepository.
 * This class provides centralized storage for all JPQL queries related to habit execution operations.
 */
public final class HabitExecutionRepositoryQueries {
  
  private HabitExecutionRepositoryQueries() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  /**
   * JPQL query for retrieving executions by habit ID.
   */
  public static final String GET_BY_HABIT_ID =
    "SELECT e FROM HabitExecution e WHERE e.habitId = :habitId ORDER BY e.date";
  
  /**
   * JPQL query for updating an execution.
   */
  public static final String UPDATE_EXECUTION =
    "UPDATE HabitExecution e SET e.date = :date, e.completed = :completed " +
    "WHERE e.id = :id";
  
  /**
   * JPQL query for retrieving an execution by ID.
   */
  public static final String GET_BY_ID =
    "SELECT e FROM HabitExecution e WHERE e.id = :id";
  
  /**
   * JPQL query for retrieving executions by habit ID and date range.
   */
  public static final String GET_BY_HABIT_AND_DATE_RANGE =
    "SELECT e FROM HabitExecution e WHERE e.habitId = :habitId " +
    "AND e.date BETWEEN :startDate AND :endDate ORDER BY e.date";
}