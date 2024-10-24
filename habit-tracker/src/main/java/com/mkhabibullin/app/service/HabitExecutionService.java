package com.mkhabibullin.app.service;

import com.mkhabibullin.app.data.HabitDbRepository;
import com.mkhabibullin.app.data.HabitExecutionDbRepository;
import com.mkhabibullin.app.model.Habit;
import com.mkhabibullin.app.model.HabitExecution;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service class for managing habit executions in a habit tracking application.
 * This class provides methods for marking habit executions, retrieving execution history,
 * calculating statistics, and generating progress reports.
 */
public class HabitExecutionService {
  private HabitExecutionDbRepository executionRepository;
  private HabitDbRepository habitRepository;
  
  /**
   * Constructs a new HabitExecutionService with the specified repositories.
   *
   * @param executionRepository the repository for habit execution data
   * @param habitRepository     the repository for habit data
   */
  public HabitExecutionService(HabitExecutionDbRepository executionRepository, HabitDbRepository habitRepository) {
    this.executionRepository = executionRepository;
    this.habitRepository = habitRepository;
  }
  
  /**
   * Marks a habit as executed (completed or not) on a specific date.
   *
   * @param habitId   the ID of the habit
   * @param date      the date of execution
   * @param completed whether the habit was completed
   */
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
  public List<HabitExecution> getHabitExecutionHistory(Long habitId) {
    return executionRepository.getByHabitId(habitId);
  }
  
  /**
   * Calculates the current streak (consecutive days of completion) for a habit.
   *
   * @param habitId the ID of the habit
   * @return the current streak as an integer
   */
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
  public double getSuccessPercentage(Long habitId, LocalDate startDate, LocalDate endDate) {
    List<HabitExecution> executions = executionRepository.getByHabitId(habitId);
    List<HabitExecution> filteredExecutions = executions.stream()
      .filter(e -> !e.getDate().isBefore(startDate) && !e.getDate().isAfter(endDate))
      .toList();
    if (filteredExecutions.isEmpty()) {
      return 0.0;
    }
    long completedCount = filteredExecutions.stream().filter(HabitExecution::isCompleted).count();
    return (double) completedCount / filteredExecutions.size() * 100;
  }
  
  /**
   * Generates a detailed progress report for a habit within a specified date range.
   *
   * @param habitId   the ID of the habit
   * @param startDate the start date of the range
   * @param endDate   the end date of the range
   * @return a string containing the progress report
   * @throws IllegalArgumentException if the habit is not found
   */
  public String generateProgressReport(Long habitId, LocalDate startDate, LocalDate endDate) {
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
  
  /**
   * Determines if there's an improving trend in habit execution.
   * This method compares the completion rate of the first half of the history
   * with the second half.
   *
   * @param history A list of HabitExecution objects representing the execution history
   * @return true if the second half shows improvement, false otherwise
   */
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
   * @param habit The Habit object for which suggestions are being generated
   * @param history A list of HabitExecution objects representing the execution history
   * @return A list of String suggestions for improving the habit
   */
  public List<String> generateSuggestions(Habit habit, List<HabitExecution> history) {
    List<String> suggestions = new ArrayList<>();
    double completionRate = history.stream().filter(HabitExecution::isCompleted).count() / (double) history.size();
    suggestions.add("Suggestions for improving your '" + habit.getName() + "' habit:");
    if (completionRate < 0.5) {
      suggestions.add("- Your completion rate is below 50%. Let's work on improving that!");
      suggestions.add("- Consider breaking down '" + habit.getName() + "' into smaller, more manageable tasks.");
      suggestions.add("- Set reminders on your phone or leave notes to help you remember to " + habit.getName() + ".");
      if (habit.getFrequency() == Habit.Frequency.DAILY) {
        suggestions.add("- For daily habits like this, try linking it to an existing daily routine.");
      }
    } else if (completionRate < 0.8) {
      suggestions.add("- You're doing well with a completion rate above 50%! Let's aim even higher.");
      suggestions.add("- Reflect on what's working well when you successfully complete '" + habit.getName() + "'.");
      suggestions.add("- Consider rewarding yourself after each successful week to reinforce the habit.");
      if (habit.getFrequency() == Habit.Frequency.WEEKLY) {
        suggestions.add("- For weekly habits, try to establish a specific day and time for '" + habit.getName() + "'.");
      }
    } else {
      suggestions.add("- Excellent work! You're consistently keeping up with '" + habit.getName() + "'.");
      suggestions.add("- Consider increasing the challenge. Can you extend the duration or intensity of '" + habit.getName() + "'?");
      suggestions.add("- Share your success with friends or family to stay motivated and inspire others.");
      if (habit.getFrequency() == Habit.Frequency.DAILY) {
        suggestions.add("- For daily habits you're mastering, consider adding a related habit to build upon your success.");
      }
    }
    if (habit.getDescription().toLowerCase().contains("exercise") ||
        habit.getDescription().toLowerCase().contains("workout")) {
      suggestions.add("- For exercise habits, remember to vary your routine to stay engaged and work different muscle groups.");
      suggestions.add("- Consider tracking additional metrics like duration or intensity to see your progress over time.");
    }
    if (habit.getDescription().toLowerCase().contains("read") ||
        habit.getDescription().toLowerCase().contains("study")) {
      suggestions.add("- For reading or studying habits, try the Pomodoro Technique: 25 minutes of focused work followed by a 5-minute break.");
      suggestions.add("- Keep a log of what you've read or learned to see your progress and stay motivated.");
    }
    long daysSinceCreation = ChronoUnit.DAYS.between(habit.getCreationDate(), LocalDate.now());
    if (daysSinceCreation > 30) {
      suggestions.add("- You've been working on this habit for over a month. Take a moment to reflect on how far you've come!");
    } else {
      suggestions.add("- This habit is still relatively new. Be patient and consistent, and you'll see results over time.");
    }
    return suggestions;
  }
}
