package com.mkhabibullin;

import com.mkhabibullin.application.service.HabitExecutionService;
import com.mkhabibullin.domain.model.Habit;
import com.mkhabibullin.domain.model.HabitExecution;
import com.mkhabibullin.infrastructure.persistence.repository.HabitDbRepository;
import com.mkhabibullin.infrastructure.persistence.repository.HabitExecutionDbRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.*;

public class HabitExecutionServiceTest {
  @Mock
  private HabitExecutionDbRepository executionRepository;
  @Mock
  private HabitDbRepository habitRepository;
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
  @DisplayName("isImprovingTrend should return true when trend is improving")
  void isImprovingTrendShouldReturnTrueWhenTrendIsImproving() {
    Long habitId = 1000L;
    List<HabitExecution> executions = Arrays.asList(
      new HabitExecution(habitId, LocalDate.of(2024, 1, 1), false),
      new HabitExecution(habitId, LocalDate.of(2024, 1, 2), false),
      new HabitExecution(habitId, LocalDate.of(2024, 1, 3), true),
      new HabitExecution(habitId, LocalDate.of(2024, 1, 4), true)
    );
    boolean improving = habitExecutionService.isImprovingTrend(executions);
    assertThat(improving).isTrue();
  }
  
  @Test
  @DisplayName("isImprovingTrend should return false when trend is not improving")
  void isImprovingTrendShouldReturnFalseWhenTrendIsNotImproving() {
    Long habitId = 1000L;
    List<HabitExecution> executions = Arrays.asList(
      new HabitExecution(habitId, LocalDate.of(2023, 1, 1), true),
      new HabitExecution(habitId, LocalDate.of(2023, 1, 2), true),
      new HabitExecution(habitId, LocalDate.of(2023, 1, 3), false),
      new HabitExecution(habitId, LocalDate.of(2023, 1, 4), false)
    );
    boolean improving = habitExecutionService.isImprovingTrend(executions);
    assertThat(improving).isFalse();
  }
  
  @Test
  @DisplayName("calculateLongestStreak should return correct longest streak")
  void calculateLongestStreakShouldReturnCorrectLongestStreak() {
    Long habitId = 1000L;
    List<HabitExecution> executions = Arrays.asList(
      new HabitExecution(habitId, LocalDate.of(2023, 1, 1), true),
      new HabitExecution(habitId, LocalDate.of(2023, 1, 2), true),
      new HabitExecution(habitId, LocalDate.of(2023, 1, 3), true),
      new HabitExecution(habitId, LocalDate.of(2023, 1, 4), false),
      new HabitExecution(habitId, LocalDate.of(2023, 1, 5), true),
      new HabitExecution(habitId, LocalDate.of(2023, 1, 6), true)
    );
    int longestStreak = habitExecutionService.calculateLongestStreak(executions);
    assertThat(longestStreak).isEqualTo(3);
  }
  
  @Test
  @DisplayName("generateSuggestions should return appropriate suggestions for low completion rate")
  void generateSuggestionsShouldReturnAppropriateForLowCompletionRate() {
    Long habitId = 1000L;
    Habit habit = new Habit();
    habit.setId(habitId);
    habit.setName("Daily Exercise");
    habit.setDescription("30 minutes of exercise");
    habit.setFrequency(Habit.Frequency.DAILY);
    habit.setCreationDate(LocalDate.now().minusDays(15));
    List<HabitExecution> executions = Arrays.asList(
      new HabitExecution(habitId, LocalDate.now().minusDays(2), false),
      new HabitExecution(habitId, LocalDate.now().minusDays(1), false),
      new HabitExecution(habitId, LocalDate.now(), true)
    );
    List<String> suggestions = habitExecutionService.generateSuggestions(habit, executions);
    assertThat(suggestions)
      .contains("Suggestions for improving your 'Daily Exercise' habit:")
      .contains("- Your completion rate is below 50%. Let's work on improving that!")
      .contains("- Consider breaking down 'Daily Exercise' into smaller, more manageable tasks.")
      .contains("- For daily habits like this, try linking it to an existing daily routine.")
      .contains("- For exercise habits, remember to vary your routine to stay engaged and work different muscle groups.")
      .contains("- This habit is still relatively new. Be patient and consistent, and you'll see results over time.");
  }
  
  @Test
  @DisplayName("generateSuggestions should return appropriate suggestions for high completion rate")
  void generateSuggestionsShouldReturnAppropriateForHighCompletionRate() {
    Long habitId = 1000L;
    Habit habit = new Habit();
    habit.setId(habitId);
    habit.setName("Weekly Reading");
    habit.setDescription("Read a book chapter");
    habit.setFrequency(Habit.Frequency.WEEKLY);
    habit.setCreationDate(LocalDate.now().minusDays(60));
    List<HabitExecution> executions = Arrays.asList(
      new HabitExecution(habitId, LocalDate.now().minusDays(14), true),
      new HabitExecution(habitId, LocalDate.now().minusDays(7), true),
      new HabitExecution(habitId, LocalDate.now(), true)
    );
    List<String> suggestions = habitExecutionService.generateSuggestions(habit, executions);
    assertThat(suggestions)
      .contains("Suggestions for improving your 'Weekly Reading' habit:")
      .contains("- Excellent work! You're consistently keeping up with 'Weekly Reading'.")
      .contains("- Consider increasing the challenge. Can you extend the duration or intensity of 'Weekly Reading'?")
      .contains("- Share your success with friends or family to stay motivated and inspire others.")
      .contains("- For reading or studying habits, try the Pomodoro Technique: 25 minutes of focused work followed by a 5-minute break.")
      .contains("- Keep a log of what you've read or learned to see your progress and stay motivated.")
      .contains("- You've been working on this habit for over a month. Take a moment to reflect on how far you've come!");
  }
  
  @Test
  @DisplayName("generateProgressReport should return correct report for valid habit")
  void generateProgressReportShouldReturnCorrectReportForValidHabit() {
    Long habitId = 1000L;
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
    assertThat(report).isNotNull()
      .contains("Progress Report for: Test Habit")
      .contains("Current Streak: 0 days")
      .contains("Success Rate: 66,67%")
      .contains("2023-01-01: Completed")
      .contains("2023-01-02: Not completed")
      .contains("2023-01-03: Completed");
    verify(habitRepository).getById(habitId);
    verify(executionRepository, times(3)).getByHabitId(habitId);
  }
  
  @Test
  @DisplayName("getCurrentStreak should return correct streak for valid habit")
  void getCurrentStreakShouldReturnCorrectStreakForValidHabit() {
    Long habitId = 1000L;
    List<HabitExecution> executions = Arrays.asList(
      new HabitExecution(habitId, LocalDate.now(), true),
      new HabitExecution(habitId, LocalDate.now().minusDays(1), true),
      new HabitExecution(habitId, LocalDate.now().minusDays(2), true),
      new HabitExecution(habitId, LocalDate.now().minusDays(3), false)
    );
    when(executionRepository.getByHabitId(habitId)).thenReturn(executions);
    int streak = habitExecutionService.getCurrentStreak(habitId);
    assertThat(streak).isEqualTo(3);
    verify(executionRepository).getByHabitId(habitId);
  }
  
  @Test
  @DisplayName("getSuccessPercentage should return correct percentage for valid habit")
  void getSuccessPercentageShouldReturnCorrectPercentageForValidHabit() {
    Long habitId = 1000L;
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
    assertThat(percentage).isCloseTo(80.0, within(0.01));
    verify(executionRepository).getByHabitId(habitId);
  }
}
