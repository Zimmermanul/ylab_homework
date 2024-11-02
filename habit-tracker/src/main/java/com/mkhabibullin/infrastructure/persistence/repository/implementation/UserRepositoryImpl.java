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
 * Provides JPA-based implementation for managing user entries.
 */
@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {
  private static final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);
  
  @PersistenceContext
  private EntityManager entityManager;
  
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