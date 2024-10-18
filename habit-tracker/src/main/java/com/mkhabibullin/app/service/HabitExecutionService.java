package com.mkhabibullin.app.service;

import com.mkhabibullin.app.data.HabitExecutionRepository;
import com.mkhabibullin.app.data.HabitRepository;
import com.mkhabibullin.app.model.Habit;
import com.mkhabibullin.app.model.HabitExecution;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
/**
 * A service that encapsulates the application's business logic related to habit executions
 */
public class HabitExecutionService {
  private HabitExecutionRepository executionRepository;
  private HabitRepository habitRepository;
  
  public HabitExecutionService(HabitExecutionRepository executionRepository, HabitRepository habitRepository) {
    this.executionRepository = executionRepository;
    this.habitRepository = habitRepository;
  }
  
  public void markHabitExecution(String habitId, LocalDate date, boolean completed) throws IOException {
    HabitExecution execution = new HabitExecution(habitId, date, completed);
    executionRepository.save(execution);
  }
  
  public List<HabitExecution> getHabitExecutionHistory(String habitId) throws IOException {
    return executionRepository.getByHabitId(habitId);
  }
  
  public double getHabitCompletionRate(String habitId, LocalDate startDate, LocalDate endDate) throws IOException {
    List<HabitExecution> executions = executionRepository.getByHabitId(habitId);
    List<HabitExecution> filteredExecutions = executions.stream()
      .filter(e -> !e.getDate().isBefore(startDate) && !e.getDate().isAfter(endDate))
      .collect(Collectors.toList());
    
    if (filteredExecutions.isEmpty()) {
      return 0.0;
    }
    long completedCount = filteredExecutions.stream().filter(HabitExecution::isCompleted).count();
    return (double) completedCount / filteredExecutions.size();
  }
  
  public int getCurrentStreak(String habitId) throws IOException {
    List<HabitExecution> executions = executionRepository.getByHabitId(habitId);
    executions.sort(Comparator.comparing(HabitExecution::getDate).reversed());
    int streak = 0;
    LocalDate currentDate = LocalDate.now();
    for (HabitExecution execution : executions) {
      if (execution.getDate().isEqual(currentDate) && execution.isCompleted()) {
        streak++;
        currentDate = currentDate.minusDays(1);
      } else if (execution.getDate().isEqual(currentDate) && !execution.isCompleted()) {
        break;
      } else if (execution.getDate().isBefore(currentDate)) {
        break;
      }
    }
    return streak;
  }
  
  public double getSuccessPercentage(String habitId, LocalDate startDate, LocalDate endDate) throws IOException {
    List<HabitExecution> executions = executionRepository.getByHabitId(habitId);
    List<HabitExecution> filteredExecutions = executions.stream()
      .filter(e -> !e.getDate().isBefore(startDate) && !e.getDate().isAfter(endDate))
      .collect(Collectors.toList());
    if (filteredExecutions.isEmpty()) {
      return 0.0;
    }
    long completedCount = filteredExecutions.stream().filter(HabitExecution::isCompleted).count();
    return (double) completedCount / filteredExecutions.size() * 100;
  }
  
  public String generateProgressReport(String habitId, LocalDate startDate, LocalDate endDate) throws IOException {
    Habit habit = habitRepository.getById(habitId);
    if (habit == null) {
      throw new IllegalArgumentException("Habit not found");
    }
    int currentStreak = getCurrentStreak(habitId);
    double successPercentage = getSuccessPercentage(habitId, startDate, endDate);
    List<HabitExecution> executions = executionRepository.getByHabitId(habitId);
    StringBuilder report = new StringBuilder();
    report.append("Progress Report for: ").append(habit.getName()).append("\n");
    report.append("Period: ").append(startDate).append(" to ").append(endDate).append("\n");
    report.append("Current Streak: ").append(currentStreak).append(" days\n");
    report.append("Success Rate: ").append(String.format("%.2f%%", successPercentage)).append("\n");
    report.append("Execution History:\n");
    executions.stream()
      .filter(e -> !e.getDate().isBefore(startDate) && !e.getDate().isAfter(endDate))
      .sorted(Comparator.comparing(HabitExecution::getDate))
      .forEach(e -> report.append(e.getDate()).append(": ")
        .append(e.isCompleted() ? "Completed" : "Not completed").append("\n"));
    return report.toString();
  }
}
