package com.mkhabibullin.app.infrastructure.persistence.queries;

/**
 * Contains SQL query constants used by the HabitDbRepository.
 * This class provides centralized storage for all SQL queries related to habit operations.
 * It cannot be instantiated as it only serves as a container for static constants.
 */
public final class HabitRepositoryQueries {
  
  private HabitRepositoryQueries() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  /**
   * SQL query for creating a new habit record.
   * This query inserts a new record into the habits table and returns the generated ID.
   * Required parameters:
   * 1. user_id (Long)
   * 2. name (String)
   * 3. description (String)
   * 4. frequency (String)
   * 5. creation_date (Date)
   * 6. is_active (Boolean)
   */
  public static final String CREATE_HABIT =
    "INSERT INTO entity.habits (user_id, name, description, frequency, creation_date, is_active) " +
    "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
  
  /**
   * SQL query for updating an existing habit record.
   * This query updates the mutable fields of a habit record identified by its ID.
   * Required parameters:
   * 1. name (String)
   * 2. description (String)
   * 3. frequency (String)
   * 4. is_active (Boolean)
   * 5. id (Long)
   */
  public static final String UPDATE_HABIT =
    "UPDATE entity.habits " +
    "SET name = ?, description = ?, frequency = ?, is_active = ? " +
    "WHERE id = ?";
  
  /**
   * SQL query for deleting a habit record.
   * This query removes a habit record from the database based on its ID.
   * Required parameters:
   * 1. id (Long)
   */
  public static final String DELETE_HABIT =
    "DELETE FROM entity.habits WHERE id = ?";
  
  /**
   * SQL query for retrieving all habit records.
   * This query selects all columns from all habit records in the database.
   * No parameters required.
   */
  public static final String READ_ALL_HABITS =
    "SELECT * FROM entity.habits";
  
  /**
   * SQL query for retrieving all habits belonging to a specific user.
   * This query selects all habit records associated with a given user ID.
   * Required parameters:
   * 1. user_id (Long)
   */
  public static final String GET_HABITS_BY_USER_ID =
    "SELECT * FROM entity.habits WHERE user_id = ?";
  
  /**
   * SQL query for retrieving a specific habit by its ID.
   * This query selects a single habit record matching the provided ID.
   * Required parameters:
   * 1. id (Long)
   */
  public static final String GET_HABIT_BY_ID =
    "SELECT * FROM entity.habits WHERE id = ?";
}