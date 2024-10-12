package com.mkhabibullin.habit;
import com.mkhabibullin.habitManagement.data.HabitExecutionRepository;
import com.mkhabibullin.habitManagement.data.HabitRepository;
import com.mkhabibullin.habitManagement.model.Habit;
import com.mkhabibullin.habitManagement.model.HabitExecution;
import com.mkhabibullin.habitManagement.service.HabitExecutionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HabitExecutionServiceTest {
  @Mock
  private HabitExecutionRepository executionRepository;
  
  @Mock
  private HabitRepository habitRepository;
  private HabitExecutionService habitExecutionService;
  private AutoCloseable closeable;
  
  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    habitExecutionService = new HabitExecutionService(executionRepository, habitRepository);
  }
  
  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }
  
  @Test
  void generateProgressReportShouldReturnCorrectReportForValidHabit() throws IOException {
    String habitId = "123";
    LocalDate startDate = LocalDate.of(2023, 1, 1);
    LocalDate endDate = LocalDate.of(2023, 1, 31);
    Habit habit = new Habit();
    habit.setId(habitId);
    habit.setName("Test Habit");
    List<HabitExecution> executions = Arrays.asList(
      new HabitExecution(habitId, LocalDate.of(2023, 1, 1), true),
      new HabitExecution(habitId, LocalDate.of(2023, 1, 2), false),
      new HabitExecution(habitId, LocalDate.of(2023, 1, 3), true)
    );
    when(habitRepository.getById(habitId)).thenReturn(habit);
    when(executionRepository.getByHabitId(habitId)).thenReturn(executions);
    String report = habitExecutionService.generateProgressReport(habitId, startDate, endDate);
    assertNotNull(report);
    assertTrue(report.contains("Progress Report for: Test Habit"));
    assertTrue(report.contains("Current Streak: 0 days"));
    assertTrue(report.contains("Success Rate: 6.45%"));
    assertTrue(report.contains("2023-01-01: Completed"));
    assertTrue(report.contains("2023-01-02: Not completed"));
    assertTrue(report.contains("2023-01-03: Completed"));
    verify(habitRepository).getById(habitId);
    verify(executionRepository).getByHabitId(habitId);
  }
  
  @Test
  void generateProgressReportShouldThrowExceptionForNonexistentHabit() throws IOException {
    String habitId = "nonexistent";
    LocalDate startDate = LocalDate.of(2023, 1, 1);
    LocalDate endDate = LocalDate.of(2023, 1, 31);
    when(habitRepository.getById(habitId)).thenReturn(null);
    assertThrows(IllegalArgumentException.class, () ->
      habitExecutionService.generateProgressReport(habitId, startDate, endDate)
    );
    verify(habitRepository).getById(habitId);
    verifyNoInteractions(executionRepository);
  }
  
  @Test
  void getCurrentStreakShouldReturnCorrectStreakForValidHabit() throws IOException {
    String habitId = "123";
    List<HabitExecution> executions = Arrays.asList(
      new HabitExecution(habitId, LocalDate.now(), true),
      new HabitExecution(habitId, LocalDate.now().minusDays(1), true),
      new HabitExecution(habitId, LocalDate.now().minusDays(2), true),
      new HabitExecution(habitId, LocalDate.now().minusDays(3), false)
    );
    when(executionRepository.getByHabitId(habitId)).thenReturn(executions);
    int streak = habitExecutionService.getCurrentStreak(habitId);
    assertEquals(3, streak);
    verify(executionRepository).getByHabitId(habitId);
  }
  
  @Test
  void getSuccessPercentageShouldReturnCorrectPercentageForValidHabit() throws IOException {
    String habitId = "123";
    LocalDate startDate = LocalDate.of(2023, 1, 1);
    LocalDate endDate = LocalDate.of(2023, 1, 10);
    List<HabitExecution> executions = Arrays.asList(
      new HabitExecution(habitId, LocalDate.of(2023, 1, 1), true),
      new HabitExecution(habitId, LocalDate.of(2023, 1, 2), false),
      new HabitExecution(habitId, LocalDate.of(2023, 1, 3), true),
      new HabitExecution(habitId, LocalDate.of(2023, 1, 4), true),
      new HabitExecution(habitId, LocalDate.of(2023, 1, 5), true)
    );
    when(executionRepository.getByHabitId(habitId)).thenReturn(executions);
    double percentage = habitExecutionService.getSuccessPercentage(habitId, startDate, endDate);
    assertEquals(50.0, percentage, 0.01);
    verify(executionRepository).getByHabitId(habitId);
  }
}
