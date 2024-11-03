package com.mkhabibullin.infrastructure.persistence.repository.implementation;

import com.mkhabibullin.domain.model.User;
import com.mkhabibullin.infrastructure.persistence.queries.UserRepositoryQueries;
import com.mkhabibullin.infrastructure.persistence.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of UserRepository interface.
 * Provides JPA-based implementation for managing user entries using EntityManager.
 * Handles CRUD operations for users with error handling, logging, and email uniqueness validation.
 *
 * @see UserRepository
 */
@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {
  private static final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);
  
  @PersistenceContext
  private EntityManager entityManager;
  
  /**
   * Retrieves all users from the database.
   *
   * @return a list of all users, or an empty list if none found or if an error occurs
   */
  @Override
  public List<User> getAllUsers() {
    try {
      TypedQuery<User> query = entityManager.createQuery(
        UserRepositoryQueries.GET_ALL_USERS,
        User.class
      );
      return query.getResultList();
    } catch (Exception e) {
      log.error("Error retrieving all users: ", e);
      return new ArrayList<>();
    }
  }
  
  /**
   * Creates a new user in the database.
   * Performs email uniqueness validation before creation.
   * Performs a flush operation to ensure the user is persisted and ID is generated.
   *
   * @param user the user entity to create
   * @throws RuntimeException if a user with the same email already exists or if there is an error during creation
   */
  @Override
  public void createUser(User user) {
    try {
      if (checkEmailExists(user.getEmail())) {
        String message = "User with email " + user.getEmail() + " already exists";
        log.error(message);
        throw new RuntimeException(message);
      }
      entityManager.persist(user);
      entityManager.flush(); // to get the generated ID
    } catch (Exception e) {
      log.error("Error creating user: ", e);
      throw new RuntimeException("Error creating user", e);
    }
  }
  
  /**
   * Retrieves a user by their unique identifier.
   *
   * @param id the unique identifier of the user
   * @return the found user entity, or null if not found or if an error occurs
   */
  @Override
  public User readUserById(Long id) {
    try {
      TypedQuery<User> query = entityManager.createQuery(
        UserRepositoryQueries.GET_USER_BY_ID,
        User.class
      );
      query.setParameter("id", id);
      List<User> results = query.getResultList();
      return results.isEmpty() ? null : results.get(0);
    } catch (Exception e) {
      log.error("Error reading user by ID: ", e);
      return null;
    }
  }
  
  /**
   * Retrieves a user by their email address.
   *
   * @param email the email address of the user
   * @return the found user entity, or null if not found or if an error occurs
   */
  @Override
  public User readUserByEmail(String email) {
    try {
      TypedQuery<User> query = entityManager.createQuery(
        UserRepositoryQueries.GET_USER_BY_EMAIL,
        User.class
      );
      query.setParameter("email", email);
      List<User> results = query.getResultList();
      return results.isEmpty() ? null : results.get(0);
    } catch (Exception e) {
      log.error("Error reading user by email: ", e);
      return null;
    }
  }
  
  /**
   * Updates an existing user's information.
   * Performs email uniqueness validation before update to ensure the new email
   * is not already in use by another user.
   *
   * @param updatedUser the user entity containing updated information
   * @throws RuntimeException if the email is already in use by another user,
   *                          if the user is not found, or if there is an error during update
   */
  @Override
  public void updateUser(User updatedUser) {
    try {
      User existingUser = readUserByEmail(updatedUser.getEmail());
      if (existingUser != null && !existingUser.getId().equals(updatedUser.getId())) {
        String message = "Email address already in use: " + updatedUser.getEmail();
        log.error(message);
        throw new RuntimeException(message);
      }
      TypedQuery<User> query = entityManager.createQuery(
        UserRepositoryQueries.UPDATE_USER,
        User.class
      );
      query.setParameter("email", updatedUser.getEmail());
      query.setParameter("passwordHash", updatedUser.getPasswordHash());
      query.setParameter("salt", updatedUser.getSalt());
      query.setParameter("name", updatedUser.getName());
      query.setParameter("admin", updatedUser.isAdmin());
      query.setParameter("blocked", updatedUser.isBlocked());
      query.setParameter("id", updatedUser.getId());
      int rowsAffected = query.executeUpdate();
      if (rowsAffected == 0) {
        String message = "User not found with ID: " + updatedUser.getId();
        log.warn(message);
        throw new RuntimeException(message);
      }
    } catch (Exception e) {
      log.error("Error updating user: ", e);
      throw new RuntimeException("Error updating user", e);
    }
  }
  
  /**
   * Deletes a user by their email address.
   * If no user is found with the given email, a warning is logged.
   *
   * @param email the email address of the user to delete
   * @throws RuntimeException if there is an error during deletion
   */
  @Override
  public void deleteUser(String email) {
    try {
      TypedQuery<User> query = entityManager.createQuery(
        UserRepositoryQueries.DELETE_USER_BY_EMAIL,
        User.class
      );
      query.setParameter("email", email);
      
      int rowsAffected = query.executeUpdate();
      if (rowsAffected == 0) {
        log.warn("User not found with email: {}", email);
      }
    } catch (Exception e) {
      log.error("Error deleting user: ", e);
      throw new RuntimeException("Error deleting user", e);
    }
  }
  
  /**
   * Checks if a user with the given email already exists.
   *
   * @param email The email to check
   * @return true if email exists, false otherwise
   */
  private boolean checkEmailExists(String email) {
    try {
      TypedQuery<Boolean> query = entityManager.createQuery(
        UserRepositoryQueries.CHECK_EMAIL_EXISTS,
        Boolean.class
      );
      query.setParameter("email", email);
      return query.getSingleResult();
    } catch (Exception e) {
      log.error("Error checking email existence: ", e);
      return false;
    }
  }
}