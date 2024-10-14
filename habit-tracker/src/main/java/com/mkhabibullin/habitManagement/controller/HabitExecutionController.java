package com.mkhabibullin.habitManagement.controller;

import com.mkhabibullin.habitManagement.model.HabitExecution;
import com.mkhabibullin.habitManagement.service.HabitExecutionService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
/**
 * Controller class for processing requests, calling the corresponding methods of service classes
 */
public class HabitExecutionController {
  private HabitExecutionService executionService;
  
  public HabitExecutionController(HabitExecutionService executionService) {
    this.executionService = executionService;
  }
  
  public void markHabitExecution(String habitId, LocalDate date, boolean completed) throws IOException {
    executionService.markHabitExecution(habitId, date, completed);
  }
  
  public List<HabitExecution> getHabitExecutionHistory(String habitId) throws IOException {
    return executionService.getHabitExecutionHistory(habitId);
  }
  
  public double getHabitCompletionRate(String habitId, LocalDate startDate, LocalDate endDate) throws IOException {
    return executionService.getHabitCompletionRate(habitId, startDate, endDate);
  }
  
  public int getCurrentStreak(String habitId) throws IOException {
    return executionService.getCurrentStreak(habitId);
  }
  
  public double getSuccessPercentage(String habitId, LocalDate startDate, LocalDate endDate) throws IOException {
    return executionService.getSuccessPercentage(habitId, startDate, endDate);
  }
  
  public String generateProgressReport(String habitId, LocalDate startDate, LocalDate endDate) throws IOException {
    return executionService.generateProgressReport(habitId, startDate, endDate);
  }
}
