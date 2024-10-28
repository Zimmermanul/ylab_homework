package com.mkhabibullin.app.infrastructure.persistence.queries;

/**
 * Contains SQL query constants used by the UserDbRepository.
 * This class provides centralized storage for all SQL queries related to user operations.
 * It cannot be instantiated as it only serves as a container for static constants.
 */
public final class UserRepositoryQueries {
  
  private UserRepositoryQueries() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  /**
   * SQL query for retrieving all users from the database.
   * This query selects all columns from the users table.
   * No parameters required.
   */
  public static final String GET_ALL_USERS =
    "SELECT * FROM entity.users";
  
  /**
   * SQL query for creating a new user record.
   * This query inserts a new record into the users table and returns the generated ID.
   * Required parameters:
   * 1. email (String)
   * 2. password_hash (String)
   * 3. salt (String)
   * 4. name (String)
   * 5. is_admin (Boolean)
   * 6. is_blocked (Boolean)
   */
  public static final String CREATE_USER =
    "INSERT INTO entity.users (email, password_hash, salt, name, is_admin, is_blocked) " +
    "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
  
  /**
   * SQL query for retrieving a user by their ID.
   * This query selects a single user record matching the provided ID.
   * Required parameters:
   * 1. id (Long)
   */
  public static final String GET_USER_BY_ID =
    "SELECT * FROM entity.users WHERE id = ?";
  
  /**
   * SQL query for retrieving a user by their email address.
   * This query selects a single user record matching the provided email.
   * Required parameters:
   * 1. email (String)
   */
  public static final String GET_USER_BY_EMAIL =
    "SELECT * FROM entity.users WHERE email = ?";
  
  /**
   * SQL query for updating an existing user record.
   * This query updates all mutable fields of a user record identified by its ID.
   * Required parameters:
   * 1. email (String)
   * 2. password_hash (String)
   * 3. salt (String)
   * 4. name (String)
   * 5. is_admin (Boolean)
   * 6. is_blocked (Boolean)
   * 7. id (Long)
   */
  public static final String UPDATE_USER =
    "UPDATE entity.users " +
    "SET email = ?, password_hash = ?, salt = ?, name = ?, is_admin = ?, is_blocked = ? " +
    "WHERE id = ?";
  
  /**
   * SQL query for deleting a user by their email address.
   * This query removes a user record from the database based on the email address.
   * Required parameters:
   * 1. email (String)
   */
  public static final String DELETE_USER_BY_EMAIL =
    "DELETE FROM entity.users WHERE email = ?";
}