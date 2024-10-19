package com.mkhabibullin;

import com.mkhabibullin.app.controller.UserController;
import com.mkhabibullin.app.data.UserRepository;
import com.mkhabibullin.app.model.User;
import com.mkhabibullin.app.presentation.MainMenuConsoleInterface;
import com.mkhabibullin.app.service.UserService;

import java.io.IOException;

/**
 * Main application class that serves as the entry point for the application.
 * This class initializes necessary components and starts the application.
 */
public class App {
  private UserRepository userRepository;
  private UserService userService;
  private MainMenuConsoleInterface mainMenu;
  
  /**
   * Constructs a new Habit Tracker App instance.
   */
  public App() {
    this.userRepository = new UserRepository();
    this.userService = new UserService(userRepository);
    this.mainMenu = new MainMenuConsoleInterface();
  }
  
  /**
   * Starts the application.
   * Creates an admin user if it doesn't exist and launches the main menu.
   */
  public void start() {
    userService.createAdminUserIfNotExists();
    mainMenu.start();
  }
  
  /**
   * The main method that serves as the entry point for the application.
   * Creates an instance of App and starts it.
   *
   * @param args command line arguments (not used in this application)
   */
  public static void main(String[] args) {
    App app = new App();
    app.start();
  }
}
