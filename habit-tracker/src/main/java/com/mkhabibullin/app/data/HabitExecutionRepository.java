package com.mkhabibullin.app.data;

import com.mkhabibullin.app.model.HabitExecution;

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
 * Repository class for managing HabitExecution entities.
 * This class provides in-memory storage with periodic persistence to a file.
 */
public class HabitExecutionRepository {
  private static final Path EXECUTION_FILE = Paths.get("habit_executions.txt");
  private final Map<String, List<HabitExecution>> executionsMap = new ConcurrentHashMap<>();
  private final ScheduledExecutorService scheduler;
  
  
  /**
   * Constructs a new HabitExecutionRepository.
   * Initializes the repository by loading existing executions, scheduling periodic saves,
   * and setting up a shutdown hook.
   */
  public HabitExecutionRepository() {
    this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
      Thread t = Executors.defaultThreadFactory().newThread(r);
      t.setDaemon(true);
      return t;
    });
    loadExecutions();
    schedulePeriodicSave();
    setupShutdownHook();
  }
  
  /**
   * Saves a new habit execution to the repository.
   *
   * @param execution The HabitExecution object to be saved.
   */
  public void save(HabitExecution execution) {
    executionsMap.computeIfAbsent(execution.getHabitId(), k -> new ArrayList<>()).add(execution);
  }
  
  /**
   * Retrieves all executions for a given habit ID.
   *
   * @param habitId The ID of the habit to retrieve executions for.
   * @return A list of HabitExecution objects for the given habit ID.
   */
  public List<HabitExecution> getByHabitId(String habitId) {
    return executionsMap.getOrDefault(habitId, new ArrayList<>());
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
    persistExecutions();
  }
  
  /**
   * Manually triggers persistence of all habit executions to the file.
   * This method can be called to force an immediate save operation.
   */
  public void persistExecutions() {
    try {
      List<String> lines = executionsMap.values().stream()
        .flatMap(List::stream)
        .map(this::formatExecution)
        .collect(Collectors.toList());
      Files.write(EXECUTION_FILE, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      System.err.println("Error persisting executions: " + e.getMessage());
    }
  }
  
  private void loadExecutions() {
    try {
      if (Files.exists(EXECUTION_FILE)) {
        List<String> lines = Files.readAllLines(EXECUTION_FILE);
        for (String line : lines) {
          HabitExecution execution = parseExecution(line);
          save(execution);
        }
      }
    } catch (IOException e) {
      System.err.println("Error loading executions: " + e.getMessage());
    }
  }
  
  private void schedulePeriodicSave() {
    scheduler.scheduleAtFixedRate(this::persistExecutions, 5, 5, TimeUnit.MINUTES);
  }
  
  private void setupShutdownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
  }
  
  private HabitExecution parseExecution(String line) {
    String[] parts = line.split(",");
    HabitExecution execution = new HabitExecution(
      parts[1],  // habitId
      LocalDate.parse(parts[2]),  // date
      Boolean.parseBoolean(parts[3])  // completed
    );
    // Manually set the id to preserve the original id from the file
    try {
      java.lang.reflect.Field idField = HabitExecution.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(execution, parts[0]);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      System.err.println("Error setting id field: " + e.getMessage());
    }
    return execution;
  }
  
  private String formatExecution(HabitExecution execution) {
    return String.join(",",
      execution.getId(),
      execution.getHabitId(),
      execution.getDate().toString(),
      String.valueOf(execution.isCompleted())
    );
  }
}
