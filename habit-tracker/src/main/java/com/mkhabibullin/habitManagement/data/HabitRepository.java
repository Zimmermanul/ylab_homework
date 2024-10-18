package com.mkhabibullin.habitManagement.data;

import com.mkhabibullin.habitManagement.model.Habit;

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
 * Class responsible for creating, reading, updating, deleting habits data data with in-memory storage and periodic persistence
 */
public class HabitRepository {
  private static final Path HABIT_FILE = Paths.get("habits.txt");
  private final Map<String, Habit> habitsMap = new ConcurrentHashMap<>();
  private final ScheduledExecutorService scheduler;
  
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
  
  public void create(Habit habit) {
    habitsMap.put(habit.getId(), habit);
  }
  
  public void update(Habit habit) {
    habitsMap.put(habit.getId(), habit);
  }
  
  public void delete(String id) {
    habitsMap.remove(id);
  }
  
  public List<Habit> readAll() {
    return new ArrayList<>(habitsMap.values());
  }
  
  public List<Habit> getByUserId(String userId) {
    return habitsMap.values().stream()
      .filter(h -> h.getUserId().equals(userId))
      .collect(Collectors.toList());
  }
  
  public Habit getById(String id) {
    return habitsMap.get(id);
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
