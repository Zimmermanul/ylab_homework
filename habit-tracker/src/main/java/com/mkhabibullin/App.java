package com.mkhabibullin;

import com.mkhabibullin.app.controller.UserController;
import com.mkhabibullin.app.data.HabitRepository;
import com.mkhabibullin.app.data.UserRepository;
import com.mkhabibullin.app.model.User;
import com.mkhabibullin.app.presentation.MainMenuConsoleInterface;
import com.mkhabibullin.app.service.UserService;

import java.io.IOException;

/**
 * Entry point to the application. Run App.main() to run
 */
public class App {
  private UserRepository userRepository;
  private UserService userService;
  private UserController userController;
  private MainMenuConsoleInterface mainMenu;
  
  public App() {
    this.userRepository = new UserRepository();
    this.userService = new UserService(userRepository);
    this.userController = new UserController(userService);
    this.mainMenu = new MainMenuConsoleInterface();
  }
  
  public void start() {
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
    mainMenu.start();
  }
  
  public static void main(String[] args) {
    App app = new App();
    app.start();
  }
}
