package com.mkhabibullin.app.data;

import com.mkhabibullin.app.model.Habit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Repository class for managing Habit entities.
 * This class provides in-memory storage with periodic persistence to a file.
 */
public class HabitRepository {
  private static final Path HABIT_FILE = Paths.get("habits.txt");
  private final Map<String, Habit> habitsMap = new ConcurrentHashMap<>();
  private final ScheduledExecutorService scheduler;
  
  /**
   * Constructs a new HabitRepository.
   * Initializes the repository by loading existing habits, scheduling periodic saves,
   * and setting up a shutdown hook.
   */
  public HabitRepository() {
    this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
      Thread t = Executors.defaultThreadFactory().newThread(r);
      t.setDaemon(true);
      return t;
    });
    loadHabits();
    schedulePeriodicSave();
    setupShutdownHook();
  }
  
  /**
   * Creates a new habit in the repository.
   *
   * @param habit The Habit object to be created.
   */
  public void create(Habit habit) {
    habitsMap.put(habit.getId(), habit);
  }
  
  /**
   * Updates an existing habit in the repository.
   *
   * @param habit The Habit object with updated information.
   */
  public void update(Habit habit) {
    habitsMap.put(habit.getId(), habit);
  }
  
  /**
   * Deletes a habit from the repository by its ID.
   *
   * @param id The ID of the habit to delete.
   */
  public void delete(String id) {
    habitsMap.remove(id);
  }
  
  /**
   * Retrieves all habits in the repository.
   *
   * @return A list of all Habit objects.
   */
  public List<Habit> readAll() {
    return new ArrayList<>(habitsMap.values());
  }
  
  /**
   * Retrieves all habits for a given user ID.
   *
   * @param userId The ID of the user to retrieve habits for.
   * @return A list of Habit objects for the given user ID.
   */
  public List<Habit> getByUserId(String userId) {
    return habitsMap.values().stream()
      .filter(h -> h.getUserId().equals(userId))
      .collect(Collectors.toList());
  }
  
  /**
   * Retrieves a habit by its ID.
   *
   * @param id The ID of the habit to retrieve.
   * @return The Habit object if found, null otherwise.
   */
  public Habit getById(String id) {
    return habitsMap.get(id);
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
    persistHabits();
  }
  
  private void loadHabits() {
    try {
      if (Files.exists(HABIT_FILE)) {
        List<String> lines = Files.readAllLines(HABIT_FILE);
        for (String line : lines) {
          try {
            Habit habit = parseHabit(line);
            habitsMap.put(habit.getId(), habit);
          } catch (IllegalArgumentException e) {
            System.err.println("Error parsing habit: " + e.getMessage());
          }
        }
      }
    } catch (IOException e) {
      System.err.println("Error loading habits: " + e.getMessage());
    }
  }
  
  private void schedulePeriodicSave() {
    scheduler.scheduleAtFixedRate(this::persistHabits, 5, 5, TimeUnit.MINUTES);
  }
  
  private void setupShutdownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
  }
  
  private void persistHabits() {
    try {
      List<String> lines = habitsMap.values().stream()
        .map(this::formatHabit)
        .collect(Collectors.toList());
      Files.write(HABIT_FILE, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      System.err.println("Error persisting habits: " + e.getMessage());
    }
  }
  
  private Habit parseHabit(String line) {
    String[] parts = line.split(",");
    if (parts.length != 7) {
      throw new IllegalArgumentException("Invalid habit data: " + line);
    }
    Habit habit = new Habit();
    habit.setId(parts[0]);
    habit.setUserId(parts[1]);
    habit.setName(parts[2]);
    habit.setDescription(parts[3]);
    try {
      habit.setFrequency(Habit.Frequency.valueOf(parts[4].toUpperCase()));
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid frequency value: " + parts[4], e);
    }
    habit.setCreationDate(LocalDate.parse(parts[5]));
    habit.setActive(Boolean.parseBoolean(parts[6]));
    return habit;
  }
  
  private String formatHabit(Habit habit) {
    return String.join(",",
      habit.getId(),
      habit.getUserId(),
      habit.getName(),
      habit.getDescription(),
      habit.getFrequency().toString(),
      habit.getCreationDate().toString(),
      String.valueOf(habit.isActive())
    );
  }
}
