package com.mkhabibullin.habitManagement.data;

import com.mkhabibullin.habitManagement.model.HabitExecution;

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
 * Class responsible for managing habit execution data with in-memory storage and periodic persistence
 */
public class HabitExecutionRepository {
  private static final Path EXECUTION_FILE = Paths.get("habit_executions.txt");
  private final Map<String, List<HabitExecution>> executionsMap = new ConcurrentHashMap<>();
  private final ScheduledExecutorService scheduler;
  
  
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
  
  public void save(HabitExecution execution) {
    executionsMap.computeIfAbsent(execution.getHabitId(), k -> new ArrayList<>()).add(execution);
  }
  
  public List<HabitExecution> getByHabitId(String habitId) {
    return executionsMap.getOrDefault(habitId, new ArrayList<>());
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
