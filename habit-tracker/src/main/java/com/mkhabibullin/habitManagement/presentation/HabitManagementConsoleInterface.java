package com.mkhabibullin.habitManagement.presentation;

import com.mkhabibullin.auth.model.User;
import com.mkhabibullin.habitManagement.controller.HabitController;
import com.mkhabibullin.habitManagement.controller.HabitExecutionController;
import com.mkhabibullin.habitManagement.model.Habit;
import com.mkhabibullin.habitManagement.model.HabitExecution;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * An interface class responsible for displaying output and handling habit management interface logic.
 */
public class HabitManagementConsoleInterface {
  private HabitController habitController;
  private HabitExecutionController executionController;
  private Scanner scanner;
  
  public HabitManagementConsoleInterface(HabitController habitController, HabitExecutionController executionController) {
    this.habitController = habitController;
    this.executionController = executionController;
    this.scanner = new Scanner(System.in);
  }
  
  public void start(User currentUser) {
    while (true) {
      showMainMenu();
      System.out.print("Choose an option: ");
      int choice = Integer.parseInt(scanner.nextLine());
      try {
        switch (choice) {
          case 1:
            createHabit(currentUser);
            break;
          case 2:
            editHabit();
            break;
          case 3:
            deleteHabit(currentUser);
            break;
          case 4:
            viewHabits(currentUser);
            break;
          case 5:
            trackHabitExecution(currentUser);
            break;
          case 6:
            viewStatistics(currentUser);
            break;
          case 7:
            generateProgressReport(currentUser);
            break;
          case 8:
            return;
          default:
            System.out.println("Invalid option. Please try again.");
        }
      } catch (IOException e) {
        System.out.println("An error occurred: " + e.getMessage());
      }
    }
  }
  
  private void showMainMenu() {
    System.out.println("\n--- Habit Management ---");
    System.out.println("1. Create Habit");
    System.out.println("2. Edit Habit");
    System.out.println("3. Delete Habit");
    System.out.println("4. View Habits");
    System.out.println("5. Track Habit Execution");
    System.out.println("6. View Statistics");
    System.out.println("7. Generate Progress Report");
    System.out.println("8. Back to Main Menu");
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
  
  private void editHabit() throws IOException {
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
    if (habits.isEmpty()){
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
      } catch (IndexOutOfBoundsException e) {
        System.out.println("Incorrect index, please try again");
      }
    } while (selectedHabit == null);
    boolean validInput = false;
    boolean delete = false;
    while (!validInput) {
      System.out.print("Are you sure you want to delete this habit? (y/n): ");
      System.out.println(selectedHabit);
      String input = scanner.nextLine().trim().toLowerCase();
      switch (input) {
        case "y":
        case "yes":
          validInput = true;
          delete = true;
          break;
        case "n":
        case "no":
          validInput = true;
          break;
        default:
          System.out.println("Invalid input. Please enter 'y' for yes or 'n' for no.");
      }
    }
    if(delete){
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
    if (habits.isEmpty()){
      System.out.println("You haven't created any habits yet");
    }
    else {
      for (Habit habit : habits) {
        System.out.println(habit);
      }
    }
  }
  
  private void trackHabitExecution(User user) throws IOException {
    List<Habit> habits = habitController.viewHabits(user.getId(), null, null);
    if (habits.isEmpty()){
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
      } catch (IndexOutOfBoundsException e) {
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
      switch (input) {
        case "y":
        case "yes":
          completed = true;
          validInput = true;
          break;
        case "n":
        case "no":
          completed = false;
          validInput = true;
          break;
        default:
          System.out.println("Invalid input. Please enter 'y' for yes or 'n' for no.");
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
    if (habits.isEmpty()){
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
        System.out.print("Choose a habit to view statistics (enter number): ");
        int habitIndex = Integer.parseInt(scanner.nextLine()) - 1;
        selectedHabit = habits.get(habitIndex);
      } catch (IndexOutOfBoundsException e) {
        System.out.println("Incorrect index, please try again");
      }
    } while (selectedHabit == null);
    LocalDate startDate = null;
    do {
      try {
        System.out.print("Enter start date (YYYY-MM-DD): ");
        startDate = LocalDate.parse(scanner.nextLine());
      } catch (DateTimeParseException e) {
        System.out.println("Incorrect format, please try again");
      }
    } while (startDate == null);
    LocalDate endDate = null;
    do {
      try {
        System.out.print("Enter end date (YYYY-MM-DD): ");
        endDate = LocalDate.parse(scanner.nextLine());
      } catch (DateTimeParseException e) {
        System.out.println("Incorrect format, please try again");
      }
    } while (endDate == null);
    int currentStreak = executionController.getCurrentStreak(selectedHabit.getId());
    double successPercentage = executionController.getSuccessPercentage(selectedHabit.getId(), startDate, endDate);
    System.out.println("\nStatistics for: " + selectedHabit.getName());
    System.out.println("Current Streak: " + currentStreak + " days");
    System.out.printf("Success Rate: %.2f%%\n", successPercentage);
    List<HabitExecution> history = executionController.getHabitExecutionHistory(selectedHabit.getId());
    System.out.println("Execution history:");
    for (HabitExecution execution : history) {
      if (!execution.getDate().isBefore(startDate) && !execution.getDate().isAfter(endDate)) {
        System.out.printf("%s: %s\n", execution.getDate(), execution.isCompleted() ? "Completed" : "Not completed");
      }
    }
  }
  
  private void generateProgressReport(User user) throws IOException {
    List<Habit> habits = habitController.viewHabits(user.getId(), null, null);
    if (habits.isEmpty()){
      System.out.println("You haven't created any habits yet");
      return;
    }
    System.out.println("Your habits:");
    for (int i = 0; i < habits.size(); i++) {
      System.out.println((i + 1) + ". " + habits.get(i).getName());
    }
    System.out.print("Choose a habit to generate a progress report (enter number): ");
    Habit selectedHabit = null;
    do {
      try {
        System.out.print("Choose a habit to view statistics (enter number): ");
        int habitIndex = Integer.parseInt(scanner.nextLine()) - 1;
        selectedHabit = habits.get(habitIndex);
      } catch (IndexOutOfBoundsException e) {
        System.out.println("Incorrect index, please try again");
      }
    } while (selectedHabit == null);
    LocalDate startDate = null;
    do {
      try {
        System.out.print("Enter start date (YYYY-MM-DD): ");
        startDate = LocalDate.parse(scanner.nextLine());
      } catch (DateTimeParseException e) {
        System.out.println("Incorrect format, please try again");
      }
    } while (startDate == null);
    LocalDate endDate = null;
    do {
      try {
        System.out.print("Enter end date (YYYY-MM-DD): ");
        endDate = LocalDate.parse(scanner.nextLine());
      } catch (DateTimeParseException e) {
        System.out.println("Incorrect format, please try again");
      }
    } while (endDate == null);
    String report = executionController.generateProgressReport(selectedHabit.getId(), startDate, endDate);
    System.out.println("\nProgress Report:");
    System.out.println(report);
  }
}
