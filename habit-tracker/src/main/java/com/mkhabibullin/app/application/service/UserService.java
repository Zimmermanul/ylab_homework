package com.mkhabibullin.app.application.service;

import com.mkhabibullin.app.domain.model.User;
import com.mkhabibullin.app.infrastructure.persistence.repository.UserDbRepository;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service class for managing users in an application.
 * This class provides methods for user registration, authentication, profile management,
 * and administrative tasks such as blocking/unblocking users.
 */
public class UserService {
  private final UserDbRepository userRepository;
  /**
   * Regular expression pattern for validating email addresses.
   */
  public static final Pattern EMAIL_PATTERN = Pattern.compile(
    "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$"
  );
  
  /**
   * Constructs a new UserService with the specified UserRepository.
   *
   * @param userRepository the repository for user data
   */
  public UserService(UserDbRepository userRepository) {
    this.userRepository = userRepository;
    
  }
  
  /**
   * Retrieves a user by their ID.
   *
   * @param id the ID of the user
   * @return the User object if found, null otherwise
   */
  public User getUserById(Long id) {
    return userRepository.readUserById(id);
  }
  
  /**
   * Retrieves a user by their email address.
   *
   * @param email the email address of the user
   * @return the User object if found, null otherwise
   * @throws IOException if there's an error reading from the repository
   */
  public User getUserByEmail(String email) throws IOException {
    return userRepository.readUserByEmail(email);
  }
  
  /**
   * Creates a new user in the system.
   *
   * @param user the User object to be created
   * @throws IOException              if there's an error writing to the repository
   * @throws IllegalArgumentException if the email is invalid or already exists
   */
  public void createUser(User user) throws IOException {
    if (!isValidEmail(user.getEmail())) {
      throw new IllegalArgumentException("Invalid email format");
    }
    if (userRepository.readUserByEmail(user.getEmail()) != null) {
      throw new IllegalArgumentException("User with this email already exists");
    }
    userRepository.createUser(user);
  }
  
  /**
   * Retrieves a list of all users in the system.
   *
   * @return a List of all User objects
   */
  public List<User> getAllUsers() {
    return userRepository.getAllUsers();
  }
  
  /**
   * Blocks a user account.
   *
   * @param email the email address of the user to be blocked
   */
  public void blockUser(String email) {
    if (!isValidEmail(email)) {
      System.out.println("Invalid email format");
      return;
    }
    User user = userRepository.readUserByEmail(email);
    if (user == null) {
      System.out.println("User not found");
      return;
    }
    if (user.isAdmin()) {
      System.out.println("Admin user can not be blocked");
      return;
    }
    user.setBlocked(true);
    userRepository.updateUser(user);
    System.out.println("User blocked successfully.");
  }
  
  /**
   * Unblocks a user account.
   *
   * @param email the email address of the user to be unblocked
   */
  public void unblockUser(String email) {
    if (!isValidEmail(email)) {
      System.out.println("Invalid email format");
      return;
    }
    User user = userRepository.readUserByEmail(email);
    if (user == null) {
      System.out.println("User not found");
      return;
    }
    if (user.isAdmin()) {
      System.out.println("Admin user can not be clocked");
      return;
    }
    user.setBlocked(false);
    userRepository.updateUser(user);
    System.out.println("User unblocked successfully.");
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
    User newUser = new User(email, name);
    newUser.setPassword(password);
    createUser(newUser);
  }
  
  /**
   * Authenticates a user.
   *
   * @param email    the email address of the user
   * @param password the password to verify
   * @return true if authentication is successful, false otherwise
   */
  public boolean authenticateUser(String email, String password) {
    if (!isValidEmail(email)) {
      return false;
    }
    User user = userRepository.readUserByEmail(email);
    return user != null && verifyPassword(password, user);
  }
  
  /**
   * Deletes a user account from the system.
   *
   * @param email the email address of the user to be deleted
   */
  public void deleteUserAccount(String email) {
    if (!isValidEmail(email)) {
      System.out.println("Invalid email format");
      return;
    }
    User user = userRepository.readUserByEmail(email);
    if (user == null) {
      System.out.println("User not found");
      return;
    }
    if (user.isAdmin()) {
      System.out.println("Admin user can not be deleted");
      return;
    }
    userRepository.deleteUser(email);
    System.out.println("User deleted successfully.");
  }
  
  private boolean isValidEmail(String email) {
    return email != null && EMAIL_PATTERN.matcher(email).matches();
  }
  
  private boolean verifyPassword(String inputPassword, User user) {
    String hashedInputPassword = hashPassword(inputPassword, user.getSalt());
    return hashedInputPassword.equals(user.getPasswordHash());
  }
  
  private String hashPassword(String password, String salt) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      md.update(Base64.getDecoder().decode(salt));
      byte[] hashedPassword = md.digest(password.getBytes());
      return Base64.getEncoder().encodeToString(hashedPassword);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error hashing password", e);
    }
  }
  
  /**
   * Updates the email address of a user.
   *
   * @param userId   the ID of the user
   * @param newEmail the new email address
   * @throws IllegalArgumentException if the new email is invalid, already in use, or the user is not found
   */
  public void updateUserEmail(Long userId, String newEmail) {
    if (!isValidEmail(newEmail)) {
      throw new IllegalArgumentException("Invalid email format");
    }
    User user = userRepository.readUserById(userId);
    if (user == null) {
      throw new IllegalArgumentException("User not found");
    }
    if (userRepository.readUserByEmail(newEmail) != null) {
      throw new IllegalArgumentException("Email already in use");
    }
    user.setEmail(newEmail);
    userRepository.updateUser(user);
  }
  
  /**
   * Updates the name of a user.
   *
   * @param userId  the ID of the user
   * @param newName the new name
   * @throws IllegalArgumentException if the user is not found
   */
  public void updateUserName(Long userId, String newName) {
    User user = userRepository.readUserById(userId);
    if (user == null) {
      throw new IllegalArgumentException("User not found");
    }
    user.setName(newName);
    userRepository.updateUser(user);
  }
  
  /**
   * Updates the password of a user.
   *
   * @param userId      the ID of the user
   * @param newPassword the new password
   * @throws IllegalArgumentException if the user is not found
   */
  public void updateUserPassword(Long userId, String newPassword) {
    User user = userRepository.readUserById(userId);
    if (user == null) {
      throw new IllegalArgumentException("User not found");
    }
    user.setPassword(newPassword);
    userRepository.updateUser(user);
  }
  
  /**
   * Creates an admin user if it doesn't already exist in the system.
   * This method is typically used for initializing the system with a default admin account.
   */
  public void createAdminUserIfNotExists() {
    try {
      if (getUserByEmail("admin@example.com") == null) {
        User adminUser = new User("admin@example.com", "Admin");
        adminUser.setPassword("adminpassword");
        adminUser.setAdmin(true);
        createUser(adminUser);
      }
    } catch (IOException e) {
      System.out.println("An error occurred while creating admin user: " + e.getMessage());
    }
  }
}