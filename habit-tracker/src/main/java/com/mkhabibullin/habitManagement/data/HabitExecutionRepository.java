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
import java.util.stream.Collectors;

public class HabitExecutionRepository {
  private static final Path EXECUTION_FILE = Paths.get("habit_executions.txt");
  
  public void save(HabitExecution execution) throws IOException {
    List<HabitExecution> executions = readAllExecutions();
    executions.add(execution);
    writeExecutions(executions);
  }
  
  public List<HabitExecution> getByHabitId(String habitId) throws IOException {
    return readAllExecutions().stream()
      .filter(e -> e.getHabitId().equals(habitId))
      .collect(Collectors.toList());
  }
  
  private List<HabitExecution> readAllExecutions() throws IOException {
    if (!Files.exists(EXECUTION_FILE)) {
      return new ArrayList<>();
    }
    List<String> lines = Files.readAllLines(EXECUTION_FILE);
    return lines.stream()
      .map(this::parseExecution)
      .collect(Collectors.toList());
  }
  
  private void writeExecutions(List<HabitExecution> executions) throws IOException {
    List<String> lines = executions.stream()
      .map(this::formatExecution)
      .collect(Collectors.toList());
    Files.write(EXECUTION_FILE, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
  }
  
  private HabitExecution parseExecution(String line) {
    String[] parts = line.split(",");
    return new HabitExecution(
      parts[1],
      LocalDate.parse(parts[2]),
      Boolean.parseBoolean(parts[3])
    );
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
