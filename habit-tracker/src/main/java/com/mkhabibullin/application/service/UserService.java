package com.mkhabibullin.application.service;

import com.mkhabibullin.domain.model.User;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service interface for managing users in an application.
 * Provides functionality for user registration, authentication, profile management,
 * and administrative tasks such as blocking/unblocking users.
 */
public interface UserService {
  /**
   * Regular expression pattern for validating email addresses.
   */
  Pattern EMAIL_PATTERN = Pattern.compile(
    "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$"
  );
  
  /**
   * Retrieves a user by their ID.
   *
   * @param id the ID of the user
   * @return the User object if found, null otherwise
   */
  User getUserById(Long id);
  
  /**
   * Retrieves a user by their email address.
   *
   * @param email the email address of the user
   * @return the User object if found, null otherwise
   */
  User getUserByEmail(String email) throws IOException;
  
  /**
   * Creates a new user in the system.
   *
   * @param user the User object to be created
   */
  void createUser(User user) throws IOException;
  
  /**
   * Retrieves a list of all users in the system.
   *
   * @return a List of all User objects
   */
  List<User> getAllUsers();
  
  /**
   * Blocks a user account.
   *
   * @param email the email address of the user to be blocked
   */
  void blockUser(String email);
  
  /**
   * Unblocks a user account.
   *
   * @param email the email address of the user to be unblocked
   */
  void unblockUser(String email);
  
  /**
   * Registers a new user in the system.
   *
   * @param email    the email address of the new user
   * @param password the password for the new user
   * @param name     the name of the new user
   */
  void registerUser(String email, String password, String name) throws IOException;
  
  /**
   * Authenticates a user.
   *
   * @param email    the email address of the user
   * @param password the password to verify
   * @return true if authentication is successful, false otherwise
   */
  boolean authenticateUser(String email, String password);
  
  /**
   * Deletes a user account from the system.
   *
   * @param email the email address of the user to be deleted
   */
  void deleteUserAccount(String email);
  
  /**
   * Updates the email address of a user.
   *
   * @param userId   the ID of the user
   * @param newEmail the new email address
   */
  void updateUserEmail(Long userId, String newEmail);
  
  /**
   * Updates the name of a user.
   *
   * @param userId  the ID of the user
   * @param newName the new name
   */
  void updateUserName(Long userId, String newName);
  
  /**
   * Updates the password of a user.
   *
   * @param userId      the ID of the user
   * @param newPassword the new password
   */
  void updateUserPassword(Long userId, String newPassword);
  
  /**
   * Creates an admin user if it doesn't already exist in the system.
   * This method is typically used for initializing the system with a default admin account.
   */
  void createAdminUserIfNotExists();
}