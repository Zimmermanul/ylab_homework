package com.mkhabibullin.app.presentation;

import com.mkhabibullin.app.controller.HabitController;
import com.mkhabibullin.app.controller.HabitExecutionController;
import com.mkhabibullin.app.controller.UserController;
import com.mkhabibullin.app.data.HabitExecutionRepository;
import com.mkhabibullin.app.data.HabitRepository;
import com.mkhabibullin.app.data.UserRepository;
import com.mkhabibullin.app.model.User;
import com.mkhabibullin.app.service.HabitExecutionService;
import com.mkhabibullin.app.service.HabitService;
import com.mkhabibullin.app.service.UserService;

import java.io.IOException;
import java.util.Scanner;

/**
 * Represents the main console interface for a habit tracking application.
 * This class initializes all necessary components and provides the main menu
 * for both admin and regular users.
 */
public class MainMenuConsoleInterface {
  private UserRepository userRepository;
  private HabitRepository habitRepository;
  private HabitExecutionRepository executionRepository;
  private UserService userService;
  private HabitService habitService;
  private HabitExecutionService executionService;
  private UserController userController;
  private HabitController habitController;
  private HabitExecutionController executionController;
  private UserManagementConsoleInterface userManagementInterface;
  private HabitManagementConsoleInterface habitManagementInterface;
  private Scanner scanner;
  
  /**
   * Constructs a new MainMenuConsoleInterface.
   * Initializes all repositories, services, controllers, and sub-interfaces.
   */
  public MainMenuConsoleInterface() {
    this.userRepository = new UserRepository();
    this.habitRepository = new HabitRepository();
    this.executionRepository = new HabitExecutionRepository();
    
    this.userService = new UserService(userRepository);
    this.habitService = new HabitService(habitRepository, userRepository);
    this.executionService = new HabitExecutionService(executionRepository, habitRepository);
    
    this.userController = new UserController(userService);
    this.habitController = new HabitController(habitService);
    this.executionController = new HabitExecutionController(executionService);
    
    this.userManagementInterface = new UserManagementConsoleInterface(userController);
    this.habitManagementInterface = new HabitManagementConsoleInterface(habitController, executionController);
    this.scanner = new Scanner(System.in);
  }
  
  /**
   * Starts the main application loop.
   * Handles user login/registration and directs to appropriate menus based on user type.
   * Continues running until the user chooses to exit.
   */
  public void start() {
    try {
      while (true) {
        User currentUser = userManagementInterface.registerOrLoginUser();
        if (currentUser == null) {
          System.out.println("Thank you for using Habit Tracker. Goodbye!");
          break;
        }
        if (currentUser.isAdmin()) {
          showMainAdminMenu(currentUser);
        } else {
          showMainUserMenu(currentUser);
        }
      }
    } catch (IOException e) {
      System.err.println("An unexpected error occurred: " + e.getMessage());
      e.printStackTrace();
    } finally {
      scanner.close();
    }
  }
  
  private void showMainAdminMenu(User admin) throws IOException {
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
        case "1" -> userManagementInterface.showUserManagementAdminMenu();
        case "2" -> habitManagementInterface.showHabitManagementMenu(admin);
        case "3" -> {
          return;
        }
        default -> System.out.println("Invalid choice. Please try again.");
      }
    }
  }
  
  private void showMainUserMenu(User user) throws IOException {
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
        case "1" -> userWasDeleted = userManagementInterface.showProfileManagementUserMenu(user);
        case "2" -> habitManagementInterface.showHabitManagementMenu(user);
        case "3" -> {
          return;
        }
        default -> System.out.println("Invalid choice. Please try again.");
      }
    }
  }
}
