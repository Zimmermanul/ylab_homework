package com.mkhabibullin.app.data;

import com.mkhabibullin.app.model.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Repository class for managing User entities.
 * This class provides in-memory storage with periodic persistence to a file.
 */
public class UserRepository {
  private static final Path USER_FILE = Paths.get("users.txt");
  private final Map<String, User> usersById = new ConcurrentHashMap<>();
  private final Map<String, User> usersByEmail = new ConcurrentHashMap<>();
  private final ScheduledExecutorService scheduler;
  
  /**
   * Constructs a new UserRepository.
   * Initializes the repository by loading existing users, scheduling periodic saves,
   * and setting up a shutdown hook.
   */
  public UserRepository() {
    this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
      Thread t = Executors.defaultThreadFactory().newThread(r);
      t.setDaemon(true);
      return t;
    });
    loadUsers();
    schedulePeriodicSave();
    setupShutdownHook();
  }
  
  /**
   * Retrieves all users in the repository.
   *
   * @return A list of all User objects.
   */
  public List<User> getAllUsers() {
    return new ArrayList<>(usersById.values());
  }
  
  /**
   * Creates a new user in the repository.
   *
   * @param user The User object to be created.
   */
  public void createUser(User user) {
    usersById.put(user.getId(), user);
    usersByEmail.put(user.getEmail(), user);
  }
  
  /**
   * Retrieves a user by their ID.
   *
   * @param id The ID of the user to retrieve.
   * @return The User object if found, null otherwise.
   */
  public User readUserById(String id) {
    return usersById.get(id);
  }
  
  /**
   * Retrieves a user by their email address.
   *
   * @param email The email of the user to retrieve.
   * @return The User object if found, null otherwise.
   */
  public User readUserByEmail(String email) {
    return usersByEmail.get(email);
  }
  
  /**
   * Updates an existing user in the repository.
   *
   * @param updatedUser The User object with updated information.
   */
  public void updateUser(User updatedUser) {
    User oldUser = usersById.get(updatedUser.getId());
    if (oldUser != null) {
      usersByEmail.remove(oldUser.getEmail());
    }
    usersById.put(updatedUser.getId(), updatedUser);
    usersByEmail.put(updatedUser.getEmail(), updatedUser);
  }
  
  /**
   * Deletes a user from the repository by their email address.
   *
   * @param email The email of the user to delete.
   */
  public void deleteUser(String email) {
    User user = usersByEmail.remove(email);
    if (user != null) {
      usersById.remove(user.getId());
    }
  }
  
  /**
   * Shuts down the repository, ensuring all data is persisted.
   * This method should be called when the application is closing.
   */
  public void shutdown() {
    scheduler.shutdown();
    try {
      if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
        scheduler.shutdownNow();
      }
    } catch (InterruptedException e) {
      scheduler.shutdownNow();
      Thread.currentThread().interrupt();
    }
    persistUsers();
  }
  
  private void loadUsers() {
    try {
      if (Files.exists(USER_FILE)) {
        List<String> lines = Files.readAllLines(USER_FILE);
        for (String line : lines) {
          User user = parseUser(line);
          usersById.put(user.getId(), user);
          usersByEmail.put(user.getEmail(), user);
        }
      }
    } catch (IOException e) {
      System.err.println("Error loading users: " + e.getMessage());
    }
  }
  
  private void schedulePeriodicSave() {
    scheduler.scheduleAtFixedRate(this::persistUsers, 5, 5, TimeUnit.MINUTES);
  }
  
  private void setupShutdownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
  }
  
  private void persistUsers() {
    try {
      List<String> lines = usersById.values().stream()
        .map(this::formatUser)
        .collect(Collectors.toList());
      Files.write(USER_FILE, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      System.err.println("Error persisting users: " + e.getMessage());
    }
  }
  
  private User parseUser(String line) {
    String[] parts = line.split(",");
    User user = new User(parts[0], parts[1], parts[3]);
    user.setPasswordHash(parts[2]);
    user.setSalt(parts[4]);
    user.setAdmin(Boolean.parseBoolean(parts[5]));
    user.setBlocked(Boolean.parseBoolean(parts[6]));
    return user;
  }
  
  private String formatUser(User user) {
    return String.join(",", user.getId(), user.getEmail(), user.getPasswordHash(), user.getName(), user.getSalt(),
      String.valueOf(user.isAdmin()), String.valueOf(user.isBlocked()));
  }
}
