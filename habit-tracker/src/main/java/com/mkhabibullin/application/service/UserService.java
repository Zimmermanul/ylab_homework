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
  User getById(Long id);
  
  /**
   * Retrieves a user by their email address.
   *
   * @param email the email address of the user
   * @return the User object if found, null otherwise
   */
  User getByEmail(String email) throws IOException;
  
  /**
   * Creates a new user in the system.
   *
   * @param user the User object to be created
   */
  void create(User user) throws IOException;
  
  /**
   * Retrieves a list of all users in the system.
   *
   * @return a List of all User objects
   */
  List<User> getAll();
  
  /**
   * Blocks a user account.
   *
   * @param email the email address of the user to be blocked
   */
  void block(String email);
  
  /**
   * Unblocks a user account.
   *
   * @param email the email address of the user to be unblocked
   */
  void unblock(String email);
  
  /**
   * Registers a new user in the system.
   *
   * @param email    the email address of the new user
   * @param password the password for the new user
   * @param name     the name of the new user
   */
  void register(String email, String password, String name) throws IOException;
  
  /**
   * Authenticates a user.
   *
   * @param email    the email address of the user
   * @param password the password to verify
   * @return true if authentication is successful, false otherwise
   */
  boolean authenticate(String email, String password);
  
  /**
   * Deletes a user account from the system.
   *
   * @param email the email address of the user to be deleted
   */
  void deleteAccount(String email);
  
  /**
   * Updates the email address of a user.
   *
   * @param userId   the ID of the user
   * @param newEmail the new email address
   */
  void updateEmail(Long userId, String newEmail);
  
  /**
   * Updates the name of a user.
   *
   * @param userId  the ID of the user
   * @param newName the new name
   */
  void updateName(Long userId, String newName);
  
  /**
   * Updates the password of a user.
   *
   * @param userId      the ID of the user
   * @param newPassword the new password
   */
  void updatePassword(Long userId, String newPassword);
}