package com.mkhabibullin.auth.presentation;

import com.mkhabibullin.auth.controller.UserController;
import com.mkhabibullin.auth.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * An interface class responsible for displaying output and handling user interface logic.
 */
public class UserConsoleInterface {
  private final UserController userController;
  private final Scanner scanner;
  
  public UserConsoleInterface(UserController userController) {
    this.userController = userController;
    this.scanner = new Scanner(System.in);
  }
  
  public User start() throws IOException {
    while (true) {
      String menu = """
        
        Welcome to the Habit Tracker!
        1. Login
        2. Register
        3. Exit
        Enter your choice (1-3):\s""";
      
      System.out.print(menu);
      
      String choice = scanner.nextLine().trim();
      switch (choice) {
        case "1" -> {
          User user = login();
          if (user != null) {
            return user;
          }
        }
        case "2" -> register();
        case "3" -> {
          return null;
        }
        default -> System.out.println("Invalid choice. Please try again.");
      }
    }
  }
  
  private User login() throws IOException {
    System.out.print("Enter email: ");
    String email = scanner.nextLine().trim();
    System.out.print("Enter password: ");
    String password = scanner.nextLine().trim();
    User user = userController.loginUser(email, password);
    if (user != null) {
      if (user.isBlocked()) {
        System.out.println("Your account has been blocked. Please contact the administrator.");
        return null;
      }
      System.out.println("Login successful!");
      return user;
    } else {
      System.out.println("Login failed. Invalid email or password.");
      return null;
    }
  }
  
  public void showAdminMenu() throws IOException {
    while (true) {
      String adminMenu = """
        
        --- Admin Menu ---
        1. List All Users
        2. Block User
        3. Unblock User
        4. Delete User
        5. Back to Main Menu
        Enter your choice (1-5):\s""";
      
      System.out.print(adminMenu);
      String choice = scanner.nextLine().trim();
      switch (choice) {
        case "1" -> listAllUsers();
        case "2" -> blockUser();
        case "3" -> unblockUser();
        case "4" -> deleteUser();
        case "5" -> {
          return;
        }
        default -> System.out.println("Invalid choice. Please try again.");
      }
    }
  }
  
  private void listAllUsers() throws IOException {
    List<User> users = userController.getAllUsers();
    System.out.println("\n--- All Users ---");
    for (User user : users) {
      System.out.printf("Email: %s, Name: %s, Admin: %s, Blocked: %s%n",
        user.getEmail(), user.getName(), user.isAdmin(), user.isBlocked());
    }
  }
  
  private void blockUser() throws IOException {
    System.out.print("Enter user email to block: ");
    String email = scanner.nextLine().trim();
    userController.blockUser(email);
  }
  
  private void unblockUser() throws IOException {
    System.out.print("Enter user email to unblock: ");
    String email = scanner.nextLine().trim();
    userController.unblockUser(email);
  }
  
  private void deleteUser() throws IOException {
    System.out.print("Enter user email to delete: ");
    String email = scanner.nextLine().trim();
    userController.deleteUserAccount(email);
  }
  
  private void register() throws IOException {
    System.out.print("Enter email: ");
    String email = getValidEmail();
    System.out.print("Enter password: ");
    String password = scanner.nextLine().trim();
    System.out.print("Enter name: ");
    String name = scanner.nextLine().trim();
    try {
      userController.registerUser(email, password, name);
      System.out.println("Registration successful! You can now login.");
    } catch (IllegalArgumentException e) {
      System.out.println("Registration failed: " + e.getMessage());
    }
  }
  
  public boolean showLoggedInMenu(User user) throws IOException {
    while (true) {
      String profileManagementMenu = """
        
        --- Profile Management ---
        1. Update Email
        2. Update Name
        3. Update Password
        4. Delete Profile
        5. Back to Main Menu
        Enter your choice (1-5):\s""";
      
      System.out.print(profileManagementMenu);
      String choice = scanner.nextLine().trim();
      switch (choice) {
        case "1" -> updateEmail(user);
        case "2" -> updateName(user);
        case "3" -> updatePassword(user);
        case "4" -> {
          if (deleteProfile(user)) {
            return true;
          }
        }
        case "5" -> {
          return false;
        }
        default -> System.out.println("Invalid choice. Please try again.");
      }
    }
  }
  
  private void updateEmail(User user) throws IOException {
    System.out.print("Enter new email: ");
    String newEmail = getValidEmail();
    try {
      userController.updateUserEmail(user.getId(), newEmail);
      System.out.println("Email updated successfully.");
    } catch (IllegalArgumentException e) {
      System.out.println("Failed to update email: " + e.getMessage());
    }
  }
  
  private void updateName(User user) throws IOException {
    System.out.print("Enter new name: ");
    String newName = scanner.nextLine().trim();
    try {
      userController.updateUserName(user.getId(), newName);
      System.out.println("Name updated successfully.");
      user.setName(newName);
    } catch (IllegalArgumentException e) {
      System.out.println("Failed to update name: " + e.getMessage());
    }
  }
  
  private void updatePassword(User user) throws IOException {
    System.out.print("Enter new password: ");
    String newPassword = scanner.nextLine().trim();
    try {
      userController.updateUserPassword(user.getId(), newPassword);
      System.out.println("Password updated successfully.");
    } catch (IllegalArgumentException e) {
      System.out.println("Failed to update password: " + e.getMessage());
    }
  }
  
  private boolean deleteProfile(User user) {
    String deleteProfile = """
      
      Are you sure you want to delete your profile? This action cannot be undone.
      Type 'YES' to confirm or any other symbol to cancel:\s""";
    
    System.out.print(deleteProfile);
    String confirmation = scanner.nextLine().trim();
    if (confirmation.equalsIgnoreCase("YES")) {
      try {
        userController.deleteUserAccount(user.getEmail());
        System.out.println("Your profile has been deleted. Goodbye!");
        return true;
      } catch (IOException e) {
        System.out.println("An error occurred while deleting your profile: " + e.getMessage());
      }
    } else {
      System.out.println("Profile deletion cancelled.");
    }
    return false;
  }
  
  private String getValidEmail() {
    while (true) {
      String email = scanner.nextLine().trim();
      if (userController.isValidEmail(email)) {
        return email;
      }
      System.out.println("Invalid email format. Please try again.");
    }
  }
}
