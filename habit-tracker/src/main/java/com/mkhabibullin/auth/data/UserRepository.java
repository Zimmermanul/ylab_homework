package com.mkhabibullin.auth.data;

import com.mkhabibullin.auth.model.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class responsible for creating, reading, updating, deleting user data
 */
public class UserRepository {
  private static final Path USER_FILE = Paths.get("users.txt");
  
  public List<User> getAllUsers() throws IOException {
    return readAllUsers();
  }
  
  public void createUser(User user) throws IOException {
    List<User> users = readAllUsers();
    users.add(user);
    writeUsers(users);
  }
  
  public User readUser(String email) throws IOException {
    return readAllUsers().stream()
      .filter(u -> u.getEmail().equals(email))
      .findFirst()
      .orElse(null);
  }
  
  public void updateUser(String currentEmail, User updatedUser) throws IOException {
    List<User> users = readAllUsers();
    List<User> updatedUsers = users.stream()
      .map(u -> u.getEmail().equals(currentEmail) ? updatedUser : u)
      .collect(Collectors.toList());
    writeUsers(updatedUsers);
  }
  
  public void deleteUser(String email) throws IOException {
    List<User> users = readAllUsers();
    List<User> updatedUsers = users.stream()
      .filter(u -> !u.getEmail().equals(email))
      .collect(Collectors.toList());
    writeUsers(updatedUsers);
  }
  
  private List<User> readAllUsers() throws IOException {
    if (!Files.exists(USER_FILE)) {
      return new ArrayList<>();
    }
    List<String> lines = Files.readAllLines(USER_FILE);
    return lines.stream()
      .map(this::parseUser)
      .collect(Collectors.toList());
  }
  
  private void writeUsers(List<User> users) throws IOException {
    List<String> lines = users.stream()
      .map(this::formatUser)
      .collect(Collectors.toList());
    Files.write(USER_FILE, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
  }
  
  private User parseUser(String line) {
    String[] parts = line.split(",");
    User user = new User(parts[0], parts[2]);
    user.setPasswordHash(parts[1]);
    user.setSalt(parts[3]);
    user.setAdmin(Boolean.parseBoolean(parts[4]));
    user.setBlocked(Boolean.parseBoolean(parts[5]));
    return user;
  }
  
  private String formatUser(User user) {
    return String.join(",", user.getEmail(), user.getPasswordHash(), user.getName(), user.getSalt(),
      String.valueOf(user.isAdmin()), String.valueOf(user.isBlocked()));
  }
}
