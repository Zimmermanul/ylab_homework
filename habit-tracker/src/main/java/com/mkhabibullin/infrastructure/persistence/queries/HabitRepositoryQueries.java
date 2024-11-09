package com.mkhabibullin.infrastructure.persistence.queries;

/**
 * Contains JPQL query constants used by the HabitRepository.
 * This class provides centralized storage for all JPQL queries related to habit operations.
 */
public final class HabitRepositoryQueries {
  
  private HabitRepositoryQueries() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  /**
   * JPQL query for retrieving all habits.
   */
  public static final String READ_ALL_HABITS =
    "SELECT h FROM Habit h";
  
  /**
   * JPQL query for retrieving habits by user ID.
   */
  public static final String GET_HABITS_BY_USER_ID =
    "SELECT h FROM Habit h WHERE h.userId = :userId";
  
  /**
   * JPQL query for retrieving a habit by ID.
   */
  public static final String GET_HABIT_BY_ID =
    "SELECT h FROM Habit h WHERE h.id = :id";
  
  /**
   * JPQL query for updating a habit.
   */
  public static final String UPDATE_HABIT =
    "UPDATE Habit h SET h.name = :name, h.description = :description, " +
    "h.frequency = :frequency, h.active = :active WHERE h.id = :id";
  
  /**
   * JPQL query for getting active habits.
   */
  public static final String GET_ACTIVE_HABITS =
    "SELECT h FROM Habit h WHERE h.active = true";
}