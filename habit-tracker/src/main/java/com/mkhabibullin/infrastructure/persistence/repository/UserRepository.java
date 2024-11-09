package com.mkhabibullin.infrastructure.persistence.repository;

import com.mkhabibullin.domain.model.User;

import java.util.List;

/**
 * Repository interface for User entities.
 * Defines CRUD operations and user authentication-related functionality.
 */
public interface UserRepository {
  
  /**
   * Retrieves all users from the database.
   *
   * @return List of all users
   */
  List<User> getAllUsers();
  
  /**
   * Creates a new user in the database.
   *
   * @param user The user to create
   * @throws RuntimeException if a user with the same email already exists
   */
  void createUser(User user);
  
  /**
   * Retrieves a user by their ID.
   *
   * @param id The ID of the user
   * @return The user if found, null otherwise
   */
  User readUserById(Long id);
  
  /**
   * Retrieves a user by their email.
   *
   * @param email The email of the user
   * @return The user if found, null otherwise
   */
  User readUserByEmail(String email);
  
  /**
   * Updates an existing user's information.
   *
   * @param updatedUser The user with updated values
   * @throws RuntimeException if the user is not found or email is already in use
   */
  void updateUser(User updatedUser);
  
  /**
   * Deletes a user by their email.
   *
   * @param email The email of the user to delete
   */
  void deleteUser(String email);
}