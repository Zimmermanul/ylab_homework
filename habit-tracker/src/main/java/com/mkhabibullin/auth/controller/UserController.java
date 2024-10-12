package com.mkhabibullin.auth.controller;

import com.mkhabibullin.auth.model.User;
import com.mkhabibullin.auth.service.UserService;

import java.io.IOException;
import java.util.List;

/**
 * Controller class for processing requests, calling the corresponding methods of service classes
 */
public class UserController {
  private final UserService userService;
  
  public UserController(UserService userService) {
    this.userService = userService;
  }
  
  public User getUser(String email) throws IOException {
    return userService.getUserByEmail(email);
  }
  
  public void createUser(User user) throws IOException {
    userService.createUser(user);
  }
  
  public void registerUser(String email, String password, String name) throws IOException {
    userService.registerUser(email, password, name);
  }
  
  public User loginUser(String email, String password) throws IOException {
    if (userService.authenticateUser(email, password)) {
      return getUser(email);
    } else return null;
  }
  
  public boolean isValidEmail(String email) {
    return email != null && UserService.EMAIL_PATTERN.matcher(email).matches();
  }
  
  public void updateUserEmail(String currentEmail, String newEmail) throws IOException {
    userService.updateUserEmail(currentEmail, newEmail);
  }
  
  public void updateUserName(String email, String newName) throws IOException {
    userService.updateUserName(email, newName);
  }
  
  public void updateUserPassword(String email, String newPassword) throws IOException {
    userService.updateUserPassword(email, newPassword);
  }
  
  public void deleteUserAccount(String email) throws IOException {
    userService.deleteUserAccount(email);
  }
  
  public List<User> getAllUsers() throws IOException {
    return userService.getAllUsers();
  }
  
  public void blockUser(String email) throws IOException {
    userService.blockUser(email);
  }
  
  public void unblockUser(String email) throws IOException {
    userService.unblockUser(email);
  }
}
