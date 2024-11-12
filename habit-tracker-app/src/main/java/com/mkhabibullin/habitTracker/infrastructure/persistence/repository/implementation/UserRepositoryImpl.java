package com.mkhabibullin.habitTracker.infrastructure.persistence.repository.implementation;

import com.mkhabibullin.habitTracker.common.MessageConstants;
import com.mkhabibullin.habitTracker.domain.exception.DuplicateEmailException;
import com.mkhabibullin.habitTracker.domain.exception.EntityNotFoundException;
import com.mkhabibullin.habitTracker.domain.exception.RepositoryException;
import com.mkhabibullin.habitTracker.domain.model.User;
import com.mkhabibullin.habitTracker.infrastructure.persistence.queries.UserRepositoryQueries;
import com.mkhabibullin.habitTracker.infrastructure.persistence.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Implementation of UserRepository interface.
 * Provides JPA-based implementation for managing user entries using EntityManager.
 * Handles CRUD operations for users with error handling, logging, and email uniqueness validation.
 *
 * @see UserRepository
 */
@Repository
@Transactional
@Slf4j
public class UserRepositoryImpl implements UserRepository {
  private static final String ENTITY_NAME = "user";
  
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
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_RETRIEVING, ENTITY_NAME),
        e
      );
    }
  }
  
  /**
   * Creates a new user in the database.
   * Performs email uniqueness validation before creation.
   * Performs a flush operation to ensure the user is persisted and ID is generated.
   *
   * @param user the user entity to create
   * @throws RepositoryException if a user with the same email already exists or if there is an error during creation
   */
  @Override
  public void createUser(User user) {
    try {
      Objects.requireNonNull(user, MessageConstants.USER_REQUIRED);
      if (checkEmailExists(user.getEmail())) {
        throw new DuplicateEmailException(
          String.format(MessageConstants.EMAIL_IN_USE, user.getEmail())
        );
      }
      entityManager.persist(user);
      entityManager.flush();
    } catch (NullPointerException | DuplicateEmailException e) {
      throw e;
    } catch (Exception e) {
      log.error("Error creating user: ", e);
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_SAVING, ENTITY_NAME),
        e
      );
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
      if (results.isEmpty()) {
        throw new EntityNotFoundException(
          String.format(MessageConstants.NOT_FOUND_WITH_ID, ENTITY_NAME, id)
        );
      }
      return results.get(0);
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      log.error("Error reading user by ID: ", e);
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_RETRIEVING, ENTITY_NAME),
        e
      );
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
      if (results.isEmpty()) {
        throw new EntityNotFoundException(
          String.format(MessageConstants.NOT_FOUND_WITH_EMAIL, ENTITY_NAME, email)
        );
      }
      return results.get(0);
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      log.error("Error reading user by email: ", e);
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_RETRIEVING_BY_EMAIL, ENTITY_NAME, email),
        e
      );
    }
  }
  
  /**
   * Updates an existing user's information.
   * Performs email uniqueness validation before update to ensure the new email
   * is not already in use by another user.
   *
   * @param updatedUser the user entity containing updated information
   * @throws RepositoryException if the email is already in use by another user,
   *                          if the user is not found, or if there is an error during update
   */
  @Override
  public void updateUser(User updatedUser) {
    try {
      Objects.requireNonNull(updatedUser, MessageConstants.USER_REQUIRED);
      User existingUser = readUserByEmail(updatedUser.getEmail());
      if (existingUser != null && !existingUser.getId().equals(updatedUser.getId())) {
        throw new DuplicateEmailException(
          String.format(MessageConstants.EMAIL_IN_USE, updatedUser.getEmail())
        );
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
        throw new EntityNotFoundException(
          String.format(MessageConstants.NOT_FOUND_WITH_ID, ENTITY_NAME, updatedUser.getId())
        );
      }
    } catch (EntityNotFoundException | DuplicateEmailException | NullPointerException e) {
      throw e;
    } catch (Exception e) {
      log.error("Error updating user: ", e);
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_UPDATING, ENTITY_NAME),
        e
      );
    }
  }
  
  /**
   * Deletes a user by their email address.
   * If no user is found with the given email, a warning is logged.
   *
   * @param email the email address of the user to delete
   * @throws RepositoryException if there is an error during deletion
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
        throw new EntityNotFoundException(
          String.format(MessageConstants.NOT_FOUND_WITH_EMAIL, ENTITY_NAME, email)
        );
      }
    } catch (EntityNotFoundException e) {
      throw e;
    } catch (Exception e) {
      log.error("Error deleting user: ", e);
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_DELETING, ENTITY_NAME),
        e
      );
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
      throw new RepositoryException(
        String.format(MessageConstants.ERROR_CHECKING_EMAIL, email),
        e
      );
    }
  }
}