package com.mkhabibullin;

import com.mkhabibullin.auth.controller.UserController;
import com.mkhabibullin.auth.data.UserRepository;
import com.mkhabibullin.auth.model.User;
import com.mkhabibullin.auth.presentation.UserConsoleInterface;
import com.mkhabibullin.auth.service.UserService;
import com.mkhabibullin.habitManagement.controller.HabitController;
import com.mkhabibullin.habitManagement.controller.HabitExecutionController;
import com.mkhabibullin.habitManagement.data.HabitExecutionRepository;
import com.mkhabibullin.habitManagement.data.HabitRepository;
import com.mkhabibullin.habitManagement.presentation.HabitManagementConsoleInterface;
import com.mkhabibullin.habitManagement.service.HabitExecutionService;
import com.mkhabibullin.habitManagement.service.HabitService;

import java.io.IOException;
import java.util.Scanner;

/**
 * Entry point to the application. Run App.main() to run
 */
public class App {
  private UserRepository userRepository;
  private HabitRepository habitRepository;
  private HabitExecutionRepository executionRepository;
  private UserService userService;
  private HabitService habitService;
  private HabitExecutionService executionService;
  private UserController userController;
  private HabitController habitController;
  private HabitExecutionController executionController;
  private UserConsoleInterface userInterface;
  private HabitManagementConsoleInterface habitInterface;
  private Scanner scanner;
  
  public App() {
    this.userRepository = new UserRepository();
    this.habitRepository = new HabitRepository();
    this.executionRepository = new HabitExecutionRepository();
    
    this.userService = new UserService(userRepository);
    this.habitService = new HabitService(habitRepository, userRepository);
    this.executionService = new HabitExecutionService(executionRepository, habitRepository);
    
    this.userController = new UserController(userService);
    this.habitController = new HabitController(habitService);
    this.executionController = new HabitExecutionController(executionService);
    
    this.userInterface = new UserConsoleInterface(userController);
    this.habitInterface = new HabitManagementConsoleInterface(habitController, executionController);
//    try {
//      habitRepository.cleanupCorruptedData();
//    } catch (IOException e) {
//      System.out.println("An error occurred while trying to clean up corrupted data (please clean file manually): " + e.getMessage());
//    }
    try {
      if (userController.getUser("admin@example.com") == null) {
        User adminUser = new User("admin@example.com", "Admin");
        adminUser.setPassword("adminpassword");
        adminUser.setAdmin(true);
        userController.createUser(adminUser);
      }
    } catch (IOException e) {
      System.out.println("An error occurred while creating admin user: " + e.getMessage());
    }
    this.scanner = new Scanner(System.in);
  }
  
  public void start() {
    try {
      while (true) {
        User currentUser = userInterface.start();
        if (currentUser == null) {
          System.out.println("Thank you for using Habit Tracker. Goodbye!");
          break;
        }
        
        if (currentUser.isAdmin()) {
          handleAdminMenu(currentUser);
        } else {
          handleUserMenu(currentUser);
        }
      }
    } catch (IOException e) {
      System.err.println("An unexpected error occurred: " + e.getMessage());
      e.printStackTrace();
    } finally {
      scanner.close();
    }
  }
  
  private void handleAdminMenu(User admin) throws IOException {
    while (true) {
      System.out.println("\n--- Admin Menu ---");
      System.out.println("1. User Management");
      System.out.println("2. Habit Management");
      System.out.println("3. Logout");
      System.out.print("Enter your choice (1-3): ");
      
      String choice = scanner.nextLine().trim();
      
      switch (choice) {
        case "1":
          userInterface.showAdminMenu();
          break;
        case "2":
          habitInterface.start(admin);
          break;
        case "3":
          return;
        default:
          System.out.println("Invalid choice. Please try again.");
      }
    }
  }
  
  private void handleUserMenu(User user) throws IOException {
    boolean userWasDeleted = false;
    while (!userWasDeleted) {
      System.out.println("\n--- User Menu ---");
      System.out.println("1. Manage Profile");
      System.out.println("2. Manage Habits");
      System.out.println("3. Logout");
      System.out.print("Enter your choice (1-3): ");
      
      String choice = scanner.nextLine().trim();
      
      switch (choice) {
        case "1":
          userWasDeleted = userInterface.showLoggedInMenu(user);
          break;
        case "2":
          habitInterface.start(user);
          break;
        case "3":
          return;
        default:
          System.out.println("Invalid choice. Please try again.");
      }
    }
  }
  
  public static void main(String[] args) {
    App app = new App();
    app.start();
  }
}
