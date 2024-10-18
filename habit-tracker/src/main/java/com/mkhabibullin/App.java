package com.mkhabibullin;

import com.mkhabibullin.app.controller.UserController;
import com.mkhabibullin.app.data.UserRepository;
import com.mkhabibullin.app.model.User;
import com.mkhabibullin.app.presentation.UserConsoleInterface;
import com.mkhabibullin.app.service.UserService;
import com.mkhabibullin.app.controller.HabitController;
import com.mkhabibullin.app.controller.HabitExecutionController;
import com.mkhabibullin.app.data.HabitExecutionRepository;
import com.mkhabibullin.app.data.HabitRepository;
import com.mkhabibullin.app.presentation.HabitManagementConsoleInterface;
import com.mkhabibullin.app.service.HabitExecutionService;
import com.mkhabibullin.app.service.HabitService;

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
    try {
      if (userController.getUserByEmail("admin@example.com") == null) {
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
      String adminMenu = """
        
        --- Admin Menu ---
        1. User Management
        2. Habit Management
        3. Logout
        Enter your choice (1-3):\s""";
      
      System.out.print(adminMenu);
      String choice = scanner.nextLine().trim();
      switch (choice) {
        case "1" -> userInterface.showAdminMenu();
        case "2" -> habitInterface.start(admin);
        case "3" -> {
          return;
        }
        default -> System.out.println("Invalid choice. Please try again.");
      }
    }
  }
  
  private void handleUserMenu(User user) throws IOException {
    boolean userWasDeleted = false;
    while (!userWasDeleted) {
      String userMenu = """
        
        --- User Menu ---
        1. Manage Profile
        2. Manage Habits
        3. Logout
        Enter your choice (1-3):\s""";
      
      System.out.print(userMenu);
      String choice = scanner.nextLine().trim();
      switch (choice) {
        case "1" -> userWasDeleted = userInterface.showLoggedInMenu(user);
        case "2" -> habitInterface.start(user);
        case "3" -> {
          return;
        }
        default -> System.out.println("Invalid choice. Please try again.");
      }
    }
  }
  
  public static void main(String[] args) {
    App app = new App();
    app.start();
  }
}
