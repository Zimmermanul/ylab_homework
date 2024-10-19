package com.mkhabibullin.app.controller;

import com.mkhabibullin.app.model.User;
import com.mkhabibullin.app.service.UserService;

import java.io.IOException;
import java.util.List;

/**
 * Controller class for managing user-related operations in an application.
 * This class acts as an intermediary between the user interface and the business logic,
 * delegating operations to the UserService.
 */
public class UserController {
  private final UserService userService;
  
  /**
   * Constructs a new UserController with the specified UserService.
   *
   * @param userService the service to handle user-related operations
   */
  public UserController(UserService userService) {
    this.userService = userService;
  }
  
  /**
   * Retrieves a user by their unique identifier.
   *
   * @param id the unique identifier of the user
   * @return the User object if found
   * @throws IOException if there's an error during the retrieval process
   */
  public User getUserById(String id) {
    return userService.getUserById(id);
  }
  
  /**
   * Retrieves a user by their email address.
   *
   * @param email the email address of the user
   * @return the User object if found
   * @throws IOException if there's an error during the retrieval process
   */
  public User getUserByEmail(String email) throws IOException {
    return userService.getUserByEmail(email);
  }
  
  /**
   * Creates a new user in the system.
   *
   * @param user the User object to be created
   * @throws IOException if there's an error during the creation process
   */
  public void createUser(User user) throws IOException {
    userService.createUser(user);
  }
  
  /**
   * Registers a new user in the system.
   *
   * @param email    the email address of the new user
   * @param password the password for the new user
   * @param name     the name of the new user
   * @throws IOException if there's an error during the registration process
   */
  public void registerUser(String email, String password, String name) throws IOException {
    userService.registerUser(email, password, name);
  }
  
  /**
   * Authenticates a user and returns their User object if successful.
   *
   * @param email    the email address of the user
   * @param password the password of the user
   * @return the User object if authentication is successful, null otherwise
   * @throws IOException if there's an error during the authentication process
   */
  public User loginUser(String email, String password) throws IOException {
    if (userService.authenticateUser(email, password)) {
      return getUserByEmail(email);
    } else return null;
  }
  
  /**
   * Validates an email address against a predefined pattern.
   *
   * @param email the email address to validate
   * @return true if the email is valid, false otherwise
   */
  public boolean isValidEmail(String email) {
    return email != null && UserService.EMAIL_PATTERN.matcher(email).matches();
  }
  
  /**
   * Updates the email address of a user.
   *
   * @param userId   the unique identifier of the user
   * @param newEmail the new email address
   * @throws IOException if there's an error during the update process
   */
  public void updateUserEmail(String userId, String newEmail) {
    userService.updateUserEmail(userId, newEmail);
  }
  
  /**
   * Updates the name of a user.
   *
   * @param userId  the unique identifier of the user
   * @param newName the new name
   * @throws IOException if there's an error during the update process
   */
  public void updateUserName(String userId, String newName) {
    userService.updateUserName(userId, newName);
  }
  
  /**
   * Updates the password of a user.
   *
   * @param userId      the unique identifier of the user
   * @param newPassword the new password
   * @throws IOException if there's an error during the update process
   */
  public void updateUserPassword(String userId, String newPassword) {
    userService.updateUserPassword(userId, newPassword);
  }
  
  /**
   * Deletes a user account from the system.
   *
   * @param email the email address of the user to be deleted
   * @throws IOException if there's an error during the deletion process
   */
  public void deleteUserAccount(String email) throws IOException {
    userService.deleteUserAccount(email);
  }
  
  /**
   * Retrieves a list of all users in the system.
   *
   * @return a List of all User objects
   * @throws IOException if there's an error during the retrieval process
   */
  public List<User> getAllUsers() {
    return userService.getAllUsers();
  }
  
  /**
   * Blocks a user account.
   *
   * @param email the email address of the user to be blocked
   * @throws IOException if there's an error during the blocking process
   */
  public void blockUser(String email) {
    userService.blockUser(email);
  }
  
  /**
   * Unblocks a user account.
   *
   * @param email the email address of the user to be unblocked
   * @throws IOException if there's an error during the unblocking process
   */
  public void unblockUser(String email) {
    userService.unblockUser(email);
  }
}