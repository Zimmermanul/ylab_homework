package com.mkhabibullin.habitTracker.application.service.implementation;

import com.mkhabibullin.habitTracker.application.service.HabitExecutionService;
import com.mkhabibullin.habitTracker.common.MessageConstants;
import com.mkhabibullin.habitTracker.domain.exception.HabitNotFoundException;
import com.mkhabibullin.habitTracker.domain.model.Habit;
import com.mkhabibullin.habitTracker.domain.model.HabitExecution;
import com.mkhabibullin.habitTracker.infrastructure.persistence.repository.HabitExecutionRepository;
import com.mkhabibullin.habitTracker.infrastructure.persistence.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Implementation of the HabitExecutionService interface that provides functionality
 * for tracking and analyzing habit executions. This implementation handles the business
 * logic for marking habits as complete, calculating streaks, generating reports,
 * and providing personalized suggestions for improvement.
 */
@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HabitExecutionServiceImpl implements HabitExecutionService {
  private final HabitExecutionRepository executionRepository;
  private final HabitRepository habitRepository;
  
  /**
   * Marks a habit as executed (completed or not) on a specific date.
   *
   * @param habitId   the ID of the habit
   * @param date      the date of execution
   * @param completed whether the habit was completed
   */
  @Override
  @Transactional
  public void markHabitExecution(Long habitId, LocalDate date, boolean completed) {
    HabitExecution execution = new HabitExecution(habitId, date, completed);
    executionRepository.save(execution);
  }
  
  /**
   * Retrieves the execution history for a specific habit.
   *
   * @param habitId the ID of the habit
   * @return a list of HabitExecution objects representing the execution history
   */
  @Override
  public List<HabitExecution> getAll(Long habitId) {
    return executionRepository.getByHabitId(habitId);
  }
  
  /**
   * Calculates the current streak (consecutive days of completion) for a habit.
   *
   * @param habitId the ID of the habit
   * @return the current streak as an integer
   */
  @Override
  public int getCurrentStreak(Long habitId) {
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
  
  /**
   * Calculates the success percentage of a habit within a specified date range.
   *
   * @param habitId   the ID of the habit
   * @param startDate the start date of the range
   * @param endDate   the end date of the range
   * @return the success percentage as a double between 0.0 and 100.0
   */
  @Override
  public double getSuccessPercentage(Long habitId, LocalDate startDate, LocalDate endDate) {
    List<HabitExecution> executions = executionRepository.getByHabitId(habitId);
    List<HabitExecution> filteredExecutions = executions.stream()
      .filter(e -> !e.getDate().isBefore(startDate) && !e.getDate().isAfter(endDate))
      .toList();
    
    if (filteredExecutions.isEmpty()) {
      return 0.0;
    }
    
    long completedCount = filteredExecutions.stream()
      .filter(HabitExecution::isCompleted)
      .count();
    return (double) completedCount / filteredExecutions.size() * 100;
  }
  
  /**
   * Generates a detailed progress report for a habit within a specified date range.
   *
   * @param habitId   the ID of the habit
   * @param startDate the start date of the range
   * @param endDate   the end date of the range
   * @return a string containing the progress report
   * @throws HabitNotFoundException if the habit is not found
   */
  @Override
  public Map<String, String> generateProgressReport(Long habitId, LocalDate startDate, LocalDate endDate) {
    Habit habit = habitRepository.getById(habitId);
    if (habit == null) {
      throw new HabitNotFoundException(String.format(MessageConstants.HABIT_NOT_FOUND, habitId));
    }
    int currentStreak = getCurrentStreak(habitId);
    double successPercentage = getSuccessPercentage(habitId, startDate, endDate);
    List<HabitExecution> executions = executionRepository.getByHabitId(habitId).stream()
      .filter(e -> !e.getDate().isBefore(startDate) && !e.getDate().isAfter(endDate))
      .sorted(Comparator.comparing(HabitExecution::getDate))
      .toList();
    StringBuilder history = new StringBuilder();
    executions.forEach(e ->
      history.append(e.getDate()).append(": ")
        .append(e.isCompleted() ? "Completed" : "Not completed").append("\n")
    );
    Map<String, String> reportData = new LinkedHashMap<>();
    reportData.put("habitName", habit.getName());
    reportData.put("startDate", startDate.toString());
    reportData.put("endDate", endDate.toString());
    reportData.put("currentStreak", currentStreak + " days");
    reportData.put("successRate", String.format("%.2f%%", successPercentage));
    reportData.put("executionHistory", history.toString());
    return reportData;
  }
  
  /**
   * Determines if there's an improving trend in habit execution.
   * This method compares the completion rate of the first half of the history
   * with the second half.
   *
   * @param history A list of HabitExecution objects representing the execution history
   * @return true if the second half shows improvement, false otherwise
   */
  @Override
  public boolean isImprovingTrend(List<HabitExecution> history) {
    int firstHalfCompleted = 0;
    int secondHalfCompleted = 0;
    int midpoint = history.size() / 2;
    
    for (int i = 0; i < history.size(); i++) {
      if (history.get(i).isCompleted()) {
        if (i < midpoint) {
          firstHalfCompleted++;
        } else {
          secondHalfCompleted++;
        }
      }
    }
    return secondHalfCompleted > firstHalfCompleted;
  }
  
  /**
   * Calculates the longest streak of consecutive completed executions in the habit history.
   *
   * @param history A list of HabitExecution objects representing the execution history
   * @return The length of the longest streak of consecutive completed executions
   */
  @Override
  public int calculateLongestStreak(List<HabitExecution> history) {
    int longestStreak = 0;
    int currentStreak = 0;
    
    for (HabitExecution execution : history) {
      if (execution.isCompleted()) {
        currentStreak++;
        longestStreak = Math.max(longestStreak, currentStreak);
      } else {
        currentStreak = 0;
      }
    }
    return longestStreak;
  }
  
  /**
   * Generates personalized suggestions for improving a habit based on its execution history.
   * This method takes into account factors such as completion rate, habit frequency,
   * habit description, and the duration since the habit was created.
   *
   * @param habit   The Habit object for which suggestions are being generated
   * @param history A list of HabitExecution objects representing the execution history
   * @return A list of String suggestions for improving the habit
   */
  @Override
  public List<String> generateSuggestions(Habit habit, List<HabitExecution> history) {
    List<String> suggestions = new ArrayList<>();
    double completionRate = history.stream()
                              .filter(HabitExecution::isCompleted)
                              .count() / (double) history.size();
    
    suggestions.add("Suggestions for improving your '" + habit.getName() + "' habit:");
    List<String> performanceSuggestions = switch (CompletionLevel.fromRate(completionRate)) {
      case LOW -> List.of(
        "- Your completion rate is below 50%. Let's work on improving that!",
        "- Consider breaking down '" + habit.getName() + "' into smaller, more manageable tasks.",
        "- Set reminders on your phone or leave notes to help you remember to " + habit.getName() + "."
      );
      case MEDIUM -> List.of(
        "- You're doing well with a completion rate above 50%! Let's aim even higher.",
        "- Reflect on what's working well when you successfully complete '" + habit.getName() + "'.",
        "- Consider rewarding yourself after each successful week to reinforce the habit."
      );
      case HIGH -> List.of(
        "- Excellent work! You're consistently keeping up with '" + habit.getName() + "'.",
        "- Consider increasing the challenge. Can you extend the duration or intensity of '" + habit.getName() + "'?",
        "- Share your success with friends or family to stay motivated and inspire others."
      );
    };
    List<String> frequencySuggestions = switch (habit.getFrequency()) {
      case DAILY -> switch (CompletionLevel.fromRate(completionRate)) {
        case LOW, MEDIUM -> List.of("- For daily habits like this, try linking it to an existing daily routine.");
        case HIGH ->
          List.of("- For daily habits you're mastering, consider adding a related habit to build upon your success.");
      };
      case WEEKLY ->
        List.of("- For weekly habits, try to establish a specific day and time for '" + habit.getName() + "'.");
    };
    List<String> categorySuggestions = Pattern.compile("\\b(exercise|workout|read|study)\\b")
      .matcher(habit.getDescription().toLowerCase())
      .results()
      .map(result -> switch (result.group().toLowerCase()) {
        case "exercise", "workout" -> List.of(
          "- For exercise habits, remember to vary your routine to stay engaged and work different muscle groups.",
          "- Consider tracking additional metrics like duration or intensity to see your progress over time."
        );
        case "read", "study" -> List.of(
          "- For reading or studying habits, try the Pomodoro Technique: 25 minutes of focused work followed by a 5-minute break.",
          "- Keep a log of what you've read or learned to see your progress and stay motivated."
        );
        default -> List.<String>of();
      })
      .flatMap(List::stream)
      .toList();
    long daysSinceCreation = ChronoUnit.DAYS.between(habit.getCreationDate(), LocalDate.now());
    List<String> timeBasedSuggestions = List.of(
      daysSinceCreation > 30
        ? "- You've been working on this habit for over a month. Take a moment to reflect on how far you've come!"
        : "- This habit is still relatively new. Be patient and consistent, and you'll see results over time."
    );
    suggestions.addAll(performanceSuggestions);
    suggestions.addAll(frequencySuggestions);
    suggestions.addAll(categorySuggestions);
    suggestions.addAll(timeBasedSuggestions);
    return suggestions;
  }
  
  private enum CompletionLevel {
    LOW(0.0, 0.5),
    MEDIUM(0.5, 0.8),
    HIGH(0.8, 1.0);
    
    private final double min;
    private final double max;
    
    CompletionLevel(double min, double max) {
      this.min = min;
      this.max = max;
    }
    
    public static CompletionLevel fromRate(double rate) {
      if (rate < 0.5) return LOW;
      if (rate < 0.8) return MEDIUM;
      return HIGH;
    }
  }
}