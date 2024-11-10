package com.mkhabibullin.infrastructure.persistence.queries;
/**
 * Contains JPQL query constants used by the UserRepository.
 * This class provides centralized storage for all JPQL queries related to user operations.
 */
public final class UserRepositoryQueries {
  
  private UserRepositoryQueries() {
    throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
  }
  
  /**
   * JPQL query for retrieving all users.
   * No parameters required.
   */
  public static final String GET_ALL_USERS =
    "SELECT u FROM User u";
  
  /**
   * JPQL query for retrieving a user by ID.
   * Required parameters:
   * - id (Long)
   */
  public static final String GET_USER_BY_ID =
    "SELECT u FROM User u WHERE u.id = :id";
  
  /**
   * JPQL query for retrieving a user by email.
   * Required parameters:
   * - email (String)
   */
  public static final String GET_USER_BY_EMAIL =
    "SELECT u FROM User u WHERE u.email = :email";
  
  /**
   * JPQL query for updating a user.
   * Required parameters:
   * - email (String)
   * - passwordHash (String)
   * - salt (String)
   * - name (String)
   * - admin (Boolean)
   * - blocked (Boolean)
   * - id (Long)
   */
  public static final String UPDATE_USER =
    "UPDATE User u SET u.email = :email, u.passwordHash = :passwordHash, " +
    "u.salt = :salt, u.name = :name, u.admin = :admin, u.blocked = :blocked " +
    "WHERE u.id = :id";
  
  /**
   * JPQL query for deleting a user by email.
   * Required parameters:
   * - email (String)
   */
  public static final String DELETE_USER_BY_EMAIL =
    "DELETE FROM User u WHERE u.email = :email";
  
  /**
   * JPQL query for checking if email exists.
   * Required parameters:
   * - email (String)
   */
  public static final String CHECK_EMAIL_EXISTS =
    "SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email";
  
  /**
   * JPQL query for retrieving active users.
   * Required parameters:
   * - blocked (Boolean = false)
   */
  public static final String GET_ACTIVE_USERS =
    "SELECT u FROM User u WHERE u.blocked = :blocked";
}