package com.mkhabibullin.auth.service;

import com.mkhabibullin.auth.data.UserRepository;
import com.mkhabibullin.auth.model.User;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.regex.Pattern;
/**
 * A service that encapsulates the application's business logic related to users (registration, authentication,
 * updating, deleting accounts)
 */
public class UserService {
  private final UserRepository userRepository;
  public static final Pattern EMAIL_PATTERN = Pattern.compile(
    "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$"
  );
  
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
    
  }
  public User getUserById(String id) throws IOException {
    return userRepository.readUserById(id);
  }
  public User getUserByEmail(String email) throws IOException {
    return userRepository.readUserByEmail(email);
  }
  
  public void createUser(User user) throws IOException {
    if (!isValidEmail(user.getEmail())) {
      throw new IllegalArgumentException("Invalid email format");
    }
    if (userRepository.readUserByEmail(user.getEmail()) != null) {
      throw new IllegalArgumentException("User with this email already exists");
    }
    userRepository.createUser(user);
  }
  
  public List<User> getAllUsers() throws IOException {
    return userRepository.getAllUsers();
  }
  
  public void blockUser(String email) throws IOException {
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
  
  public void unblockUser(String email) throws IOException {
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
  
  public void registerUser(String email, String password, String name) throws IOException {
    User newUser = new User(email, name);
    newUser.setPassword(password);
    createUser(newUser);
  }
  
  public boolean authenticateUser(String email, String password) throws IOException {
    if (!isValidEmail(email)) {
      return false;
    }
    User user = userRepository.readUserByEmail(email);
    return user != null && verifyPassword(password, user);
  }
  
  public void deleteUserAccount(String email) throws IOException {
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
  
  public void updateUserEmail(String userId, String newEmail) throws IOException {
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
  
  public void updateUserName(String userId, String newName) throws IOException {
    User user = userRepository.readUserById(userId);
    if (user == null) {
      throw new IllegalArgumentException("User not found");
    }
    user.setName(newName);
    userRepository.updateUser(user);
  }
  
  public void updateUserPassword(String userId, String newPassword) throws IOException {
    User user = userRepository.readUserById(userId);
    if (user == null) {
      throw new IllegalArgumentException("User not found");
    }
    user.setPassword(newPassword);
    userRepository.updateUser(user);
  }
}