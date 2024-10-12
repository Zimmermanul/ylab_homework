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
 * Сервис, инкапсулирующий бизнес-логику приложения, связанную с пользователями (регистрация, аутентификация,
 * обновление, удаление аккаунтов)
 */
public class UserService {
  private final UserRepository userRepository;
  public static final Pattern EMAIL_PATTERN = Pattern.compile(
    "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$"
  );
  
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
    
  }
  
  public User getUserByEmail(String email) throws IOException {
    return userRepository.readUser(email);
  }
  
  public void createUser(User user) throws IOException {
    if (!isValidEmail(user.getEmail())) {
      throw new IllegalArgumentException("Invalid email format");
    }
    if (userRepository.readUser(user.getEmail()) != null) {
      throw new IllegalArgumentException("User with this email already exists");
    }
    userRepository.createUser(user);
  }
  
  public List<User> getAllUsers() throws IOException {
    return userRepository.getAllUsers();
  }
  
  public void blockUser(String email) throws IOException {
    User user = userRepository.readUser(email);
    if (user == null) {
      throw new IllegalArgumentException("User not found");
    }
    user.setBlocked(true);
    userRepository.updateUser(email, user);
  }
  
  public void unblockUser(String email) throws IOException {
    User user = userRepository.readUser(email);
    if (user == null) {
      throw new IllegalArgumentException("User not found");
    }
    user.setBlocked(false);
    userRepository.updateUser(email, user);
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
    User user = userRepository.readUser(email);
    return user != null && verifyPassword(password, user);
  }
  
  public void deleteUserAccount(String email) throws IOException {
    if (!isValidEmail(email)) {
      throw new IllegalArgumentException("Invalid email format");
    }
    userRepository.deleteUser(email);
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
  
  public void updateUserEmail(String currentEmail, String newEmail) throws IOException {
    if (!isValidEmail(newEmail)) {
      throw new IllegalArgumentException("Invalid email format");
    }
    User user = userRepository.readUser(currentEmail);
    if (user == null) {
      throw new IllegalArgumentException("User not found");
    }
    if (userRepository.readUser(newEmail) != null) {
      throw new IllegalArgumentException("Email already in use");
    }
    user.setEmail(newEmail);
    userRepository.updateUser(currentEmail, user);
  }
  
  public void updateUserName(String email, String newName) throws IOException {
    User user = userRepository.readUser(email);
    if (user == null) {
      throw new IllegalArgumentException("User not found");
    }
    user.setName(newName);
    userRepository.updateUser(email, user);
  }
  
  public void updateUserPassword(String email, String newPassword) throws IOException {
    User user = userRepository.readUser(email);
    if (user == null) {
      throw new IllegalArgumentException("User not found");
    }
    user.setPassword(newPassword);
    userRepository.updateUser(email, user);
  }
}