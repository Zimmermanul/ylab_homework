package com.mkhabibullin.app.presentation;

import com.mkhabibullin.app.model.User;
import com.mkhabibullin.app.controller.HabitController;
import com.mkhabibullin.app.controller.HabitExecutionController;
import com.mkhabibullin.app.model.Habit;
import com.mkhabibullin.app.model.HabitExecution;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Provides a console-based user interface for managing habits in a habit tracking application.
 * This class interacts with HabitController and HabitExecutionController to perform
 * various operations related to habits.
 */
public class HabitManagementConsoleInterface {
  private HabitController habitController;
  private HabitExecutionController executionController;
  private Scanner scanner;
  
  /**
   * Constructs a new HabitManagementConsoleInterface with the specified controllers.
   *
   * @param habitController     the controller for habit-related operations
   * @param executionController the controller for habit execution-related operations
   */
  public HabitManagementConsoleInterface(HabitController habitController, HabitExecutionController executionController) {
    this.habitController = habitController;
    this.executionController = executionController;
    this.scanner = new Scanner(System.in);
  }
  
  /**
   * Displays the main habit management menu and handles user interactions.
   * This method runs in a loop until the user chooses to return to the main menu.
   *
   * @param currentUser the currently logged-in user
   */
  public void showHabitManagementMenu(User currentUser) {
    while (true) {
      String habitManagementMenu = """
        
        --- Habit Management ---
        1. Create Habit
        2. Edit Habit
        3. Delete Habit
        4. View Habits
        5. Track Habit Execution
        6. View Statistics
        7. Generate Progress Report
        8. Back to Main Menu
        Enter your choice (1-8):
        Choose an option:\s""";
      System.out.print(habitManagementMenu);
      String choice = scanner.nextLine().trim();
      try {
        switch (choice) {
          case "1" -> createHabit(currentUser);
          case "2" -> editHabit(currentUser);
          case "3" -> deleteHabit(currentUser);
          case "4" -> viewHabits(currentUser);
          case "5" -> trackHabitExecution(currentUser);
          case "6" -> viewStatistics(currentUser);
          case "7" -> generateProgressReport(currentUser);
          case "8" -> {
            return;
          }
          default -> System.out.println("Invalid option. Please try again.");
        }
      } catch (IOException e) {
        System.out.println("An error occurred: " + e.getMessage());
      }
    }
  }
  
  private void createHabit(User user) throws IOException {
    System.out.print("Enter habit name: ");
    String name = scanner.nextLine();
    System.out.print("Enter habit description: ");
    String description = scanner.nextLine();
    Habit.Frequency frequency = null;
    do {
      try {
        System.out.print("Enter frequency (DAILY/WEEKLY): ");
        frequency = Habit.Frequency.valueOf(scanner.nextLine().toUpperCase());
      } catch (IllegalArgumentException e) {
        System.out.println("Incorrect frequency, please try again");
      }
    } while (frequency == null);
    habitController.createHabit(user.getEmail(), name, description, frequency);
    System.out.println("Habit created successfully.");
  }
  
  private void editHabit(User user) throws IOException {
    List<Habit> habits = habitController.viewHabits(user.getId(), null, null);
    if (habits.isEmpty()) {
      System.out.println("You haven't created any habits yet");
    } else {
      System.out.print("Your habits: \n");
      for (Habit habit : habits) {
        System.out.println(habit);
      }
    }
    System.out.print("Enter habit ID to edit: ");
    String id = scanner.nextLine();
    System.out.print("Enter new name: ");
    String name = scanner.nextLine();
    System.out.print("Enter new description: ");
    String description = scanner.nextLine();
    Habit.Frequency frequency = null;
    do {
      try {
        System.out.print("Enter new frequency (DAILY/WEEKLY): ");
        frequency = Habit.Frequency.valueOf(scanner.nextLine().toUpperCase());
      } catch (IllegalArgumentException e) {
        System.out.println("Incorrect frequency, please try again");
      }
    } while (frequency == null);
    try {
      habitController.editHabit(id, name, description, frequency);
    } catch (IllegalArgumentException e) {
      System.out.println("Habit with this ID not found, please try again");
      return;
    }
    System.out.println("Habit edited successfully.");
  }
  
  private void deleteHabit(User user) throws IOException {
    List<Habit> habits = habitController.viewHabits(user.getId(), null, null);
    if (habits.isEmpty()) {
      System.out.println("You haven't created any habits yet");
      return;
    }
    System.out.println("Your habits:");
    for (int i = 0; i < habits.size(); i++) {
      System.out.println((i + 1) + ". " + habits.get(i).getName());
    }
    Habit selectedHabit = null;
    do {
      try {
        System.out.print("Choose a habit to delete (enter number): ");
        int habitIndex = Integer.parseInt(scanner.nextLine()) - 1;
        selectedHabit = habits.get(habitIndex);
      } catch (IndexOutOfBoundsException | NumberFormatException e) {
        System.out.println("Incorrect index, please try again");
      }
    } while (selectedHabit == null);
    boolean validInput = false;
    boolean delete = false;
    while (!validInput) {
      System.out.print("Are you sure you want to delete this habit? (y/n): ");
      System.out.println(selectedHabit);
      String input = scanner.nextLine().trim().toLowerCase();
      switch (input.toLowerCase()) {
        case "y", "yes" -> {
          validInput = true;
          delete = true;
        }
        case "n", "no" -> validInput = true;
        default -> System.out.println("Invalid input. Please enter 'y' for yes or 'n' for no.");
      }
    }
    if (delete) {
      try {
        habitController.deleteHabit(selectedHabit.getId());
        System.out.println("Habit deleted successfully");
      } catch (IOException e) {
        System.out.println("Error deleting habit: " + e.getMessage());
      }
    }
  }
  
  private void viewHabits(User user) throws IOException {
    System.out.print("Enter filter date (YYYY-MM-DD) or press enter to skip: ");
    String dateStr = scanner.nextLine();
    LocalDate filterDate = dateStr.isEmpty() ? null : LocalDate.parse(dateStr);
    System.out.print("Filter by active status (true/false) or press enter to skip: ");
    String activeStr = scanner.nextLine();
    Boolean active = activeStr.isEmpty() ? null : Boolean.parseBoolean(activeStr);
    List<Habit> habits = habitController.viewHabits(user.getId(), filterDate, active);
    if (habits.isEmpty()) {
      System.out.println("You haven't created any habits yet");
    } else {
      for (Habit habit : habits) {
        System.out.println(habit);
      }
    }
  }
  
  private void trackHabitExecution(User user) throws IOException {
    List<Habit> habits = habitController.viewHabits(user.getId(), null, null);
    if (habits.isEmpty()) {
      System.out.println("You haven't created any habits yet");
      return;
    }
    System.out.println("Your habits:");
    for (int i = 0; i < habits.size(); i++) {
      System.out.println((i + 1) + ". " + habits.get(i).getName());
    }
    Habit selectedHabit = null;
    do {
      try {
        System.out.print("Choose a habit to track (enter number): ");
        int habitIndex = Integer.parseInt(scanner.nextLine()) - 1;
        selectedHabit = habits.get(habitIndex);
      } catch (IndexOutOfBoundsException | NumberFormatException e) {
        System.out.println("Incorrect index, please try again");
      }
    } while (selectedHabit == null);
    LocalDate date = null;
    do {
      try {
        System.out.print("Enter date (YYYY-MM-DD) or press enter for today: ");
        String dateStr = scanner.nextLine();
        date = dateStr.isEmpty() ? LocalDate.now() : LocalDate.parse(dateStr);
      } catch (DateTimeParseException e) {
        System.out.println("Incorrect format, please try again");
      }
    } while (date == null);
    boolean completed = false;
    boolean validInput = false;
    
    while (!validInput) {
      System.out.print("Did you complete this habit? (y/n): ");
      String input = scanner.nextLine().trim().toLowerCase();
      switch (input.toLowerCase()) {
        case "y", "yes" -> {
          completed = true;
          validInput = true;
        }
        case "n", "no" -> {
          validInput = true;
        }
        default -> System.out.println("Invalid input. Please enter 'y' for yes or 'n' for no.");
      }
    }
    try {
      executionController.markHabitExecution(selectedHabit.getId(), date, completed);
      System.out.println("Habit execution recorded successfully.");
    } catch (IOException e) {
      System.out.println("Error recording habit execution: " + e.getMessage());
    }
  }
  
  private void viewStatistics(User user) throws IOException {
    List<Habit> habits = habitController.viewHabits(user.getId(), null, null);
    if (habits.isEmpty()) {
      System.out.println("You haven't created any habits yet");
      return;
    }
    Habit selectedHabit = selectHabit(habits, "Choose a habit to view statistics");
    LocalDate startDate = inputDate("Enter start date (YYYY-MM-DD): ");
    LocalDate endDate = inputDate("Enter end date (YYYY-MM-DD): ");
    int currentStreak = executionController.getCurrentStreak(selectedHabit.getId());
    double successPercentage = executionController.getSuccessPercentage(selectedHabit.getId(), startDate, endDate);
    List<HabitExecution> history = executionController.getHabitExecutionHistory(selectedHabit.getId());
    System.out.println("\nDetailed Statistics for: " + selectedHabit.getName());
    System.out.println("Period: " + startDate + " to " + endDate);
    System.out.println("Current Streak: " + currentStreak + " days");
    System.out.printf("Success Rate: %.2f%%\n", successPercentage);
    long totalExecutions = history.stream()
      .filter(e -> !e.getDate().isBefore(startDate) && !e.getDate().isAfter(endDate))
      .count();
    long completedExecutions = history.stream()
      .filter(e -> !e.getDate().isBefore(startDate) && !e.getDate().isAfter(endDate) && e.isCompleted())
      .count();
    System.out.println("Total Executions: " + totalExecutions);
    System.out.println("Completed Executions: " + completedExecutions);
    System.out.println("Missed Executions: " + (totalExecutions - completedExecutions));
    Map<DayOfWeek, Long> completionsByDay = history.stream()
      .filter(e -> !e.getDate().isBefore(startDate) && !e.getDate().isAfter(endDate) && e.isCompleted())
      .collect(Collectors.groupingBy(e -> e.getDate().getDayOfWeek(), Collectors.counting()));
    System.out.println("\nCompletions by Day of Week:");
    for (DayOfWeek day : DayOfWeek.values()) {
      System.out.printf("%s: %d\n", day, completionsByDay.getOrDefault(day, 0L));
    }
  }
  
  private void generateProgressReport(User user) throws IOException {
    List<Habit> habits = habitController.viewHabits(user.getId(), null, null);
    if (habits.isEmpty()) {
      System.out.println("You haven't created any habits yet");
      return;
    }
    Habit selectedHabit = selectHabit(habits, "Choose a habit to generate a progress report");
    LocalDate startDate = inputDate("Enter start date (YYYY-MM-DD): ");
    LocalDate endDate = inputDate("Enter end date (YYYY-MM-DD): ");
    String report = executionController.generateProgressReport(selectedHabit.getId(), startDate, endDate);
    System.out.println("\nProgress Report:");
    System.out.println(report);
    List<HabitExecution> history = executionController.getHabitExecutionHistory(selectedHabit.getId());
    List<HabitExecution> filteredHistory = history.stream()
      .filter(e -> !e.getDate().isBefore(startDate) && !e.getDate().isAfter(endDate))
      .sorted(Comparator.comparing(HabitExecution::getDate))
      .collect(Collectors.toList());
    System.out.println("Trend Analysis:");
    if (filteredHistory.size() >= 2) {
      boolean improving = executionController.isImprovingTrend(filteredHistory);
      System.out.println(improving ? "Your habit execution is improving over time." : "There's room for improvement in your habit execution.");
    } else {
      System.out.println("Not enough data for trend analysis.");
    }
    System.out.println("\nLongest Streak:");
    int longestStreak = executionController.calculateLongestStreak(filteredHistory);
    System.out.println("Your longest streak during this period was " + longestStreak + " days.");
    System.out.println("\nSuggestions:");
    executionController.generateSuggestions(selectedHabit, filteredHistory)
      .forEach(System.out::println);
  }
  
  private Habit selectHabit(List<Habit> habits, String prompt) {
    System.out.println("Your habits:");
    for (int i = 0; i < habits.size(); i++) {
      System.out.println((i + 1) + ". " + habits.get(i).getName());
    }
    Habit selectedHabit = null;
    do {
      try {
        System.out.print(prompt + " (enter number): ");
        int habitIndex = Integer.parseInt(scanner.nextLine()) - 1;
        selectedHabit = habits.get(habitIndex);
      } catch (IndexOutOfBoundsException | NumberFormatException e) {
        System.out.println("Incorrect index, please try again");
      }
    } while (selectedHabit == null);
    return selectedHabit;
  }
  
  private LocalDate inputDate(String prompt) {
    LocalDate date = null;
    do {
      try {
        System.out.print(prompt);
        date = LocalDate.parse(scanner.nextLine());
      } catch (DateTimeParseException e) {
        System.out.println("Incorrect format, please try again");
      }
    } while (date == null);
    return date;
  }
}
