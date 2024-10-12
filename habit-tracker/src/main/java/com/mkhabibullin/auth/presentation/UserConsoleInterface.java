package com.mkhabibullin.auth.presentation;

import com.mkhabibullin.auth.controller.UserController;
import com.mkhabibullin.auth.model.User;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Интерфейсный класс, отвечающий за отображение вывода и обработку логики пользовательского интерфейса
 */
public class UserConsoleInterface {
  private final UserController userController;
  private final Scanner scanner;
//  private String currentUserEmail;
  
  public UserConsoleInterface(UserController userController) {
    this.userController = userController;
    this.scanner = new Scanner(System.in);
  }
  
  public User start() throws IOException {
    while (true) {
      System.out.println("\nWelcome to the Habit Tracker!");
      System.out.println("1. Login");
      System.out.println("2. Register");
      System.out.println("3. Exit");
      System.out.print("Enter your choice (1-3): ");
      
      String choice = scanner.nextLine().trim();
      switch (choice) {
        case "1":
          User user = login();
          if (user != null) {
            return user;
          }
          break;
        case "2":
          register();
          break;
        case "3":
          return null;
        default:
          System.out.println("Invalid choice. Please try again.");
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
      System.out.println("\n--- Admin Menu ---");
      System.out.println("1. List All Users");
      System.out.println("2. Block User");
      System.out.println("3. Unblock User");
      System.out.println("4. Delete User");
      System.out.println("5. Back to Main Menu");
      System.out.print("Enter your choice (1-5): ");
      
      String choice = scanner.nextLine().trim();
      
      switch (choice) {
        case "1":
          listAllUsers();
          break;
        case "2":
          blockUser();
          break;
        case "3":
          unblockUser();
          break;
        case "4":
          deleteUser();
          break;
        case "5":
          return;
        default:
          System.out.println("Invalid choice. Please try again.");
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
    System.out.println("User blocked successfully.");
  }
  
  private void unblockUser() throws IOException {
    System.out.print("Enter user email to unblock: ");
    String email = scanner.nextLine().trim();
    userController.unblockUser(email);
    System.out.println("User unblocked successfully.");
  }
  
  private void deleteUser() throws IOException {
    System.out.print("Enter user email to delete: ");
    String email = scanner.nextLine().trim();
    userController.deleteUserAccount(email);
    System.out.println("User deleted successfully.");
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
      System.out.println("\n--- Profile Management ---");
      System.out.println("1. Update Email");
      System.out.println("2. Update Name");
      System.out.println("3. Update Password");
      System.out.println("4. Delete Profile");
      System.out.println("5. Back to Main Menu");
      System.out.print("Enter your choice (1-5): ");
      
      String choice = scanner.nextLine().trim();
      
      switch (choice) {
        case "1":
          updateEmail(user);
          break;
        case "2":
          updateName(user);
          break;
        case "3":
          updatePassword(user);
          break;
        case "4":
          if (deleteProfile(user)) {
            return true;
          }
          break;
        case "5":
          return false;
        default:
          System.out.println("Invalid choice. Please try again.");
      }
    }
  }

//  private void showLoggedInMenu() {
//    while (!currentUserEmail.isEmpty()) {
//      System.out.println("\n--- Main Menu ---");
//      System.out.println("1. Update Profile");
//      System.out.println("2. Delete Profile");
//      System.out.println("3. Logout");
//      System.out.print("Enter your choice (1-3): ");
//
//      String choice = scanner.nextLine().trim();
//
//      switch (choice) {
//        case "1":
//          updateProfile();
//          break;
//        case "2":
//          if (deleteProfile()) {
//            return; // Exit to main menu if profile is deleted
//          }
//          break;
//        case "3":
//          System.out.println("Logging out...");
//          logout();
//          return;
//        default:
//          System.out.println("Invalid choice. Please try again.");
//      }
//    }
//  }

//  private void updateProfile() {
//    while (!currentUserEmail.isEmpty()) {
//      System.out.println("\n--- Update Profile ---");
//      System.out.println("1. Update your email");
//      System.out.println("2. Update your name");
//      System.out.println("3. Update your password");
//      System.out.println("4. Back to main menu");
//      System.out.print("Enter your choice (1-4): ");
//
//      String choice = scanner.nextLine().trim();
//
//      try {
//        switch (choice) {
//          case "1":
//            updateEmail();
//            break;
//          case "2":
//            updateName();
//            break;
//          case "3":
//            updatePassword();
//            break;
//          case "4":
//            return;
//          default:
//            System.out.println("Invalid choice. Please try again.");
//        }
//      } catch (IOException e) {
//        System.out.println("An error occurred: " + e.getMessage());
//      }
//    }
//  }
  
  private void updateEmail(User user) throws IOException {
    System.out.print("Enter new email: ");
    String newEmail = getValidEmail();
    try {
      userController.updateUserEmail(user.getEmail(), newEmail);
      System.out.println("Email updated successfully.");
      user.setEmail(newEmail); // Update the user object
    } catch (IllegalArgumentException e) {
      System.out.println("Failed to update email: " + e.getMessage());
    }
  }
  
  private void updateName(User user) throws IOException {
    System.out.print("Enter new name: ");
    String newName = scanner.nextLine().trim();
    try {
      userController.updateUserName(user.getEmail(), newName);
      System.out.println("Name updated successfully.");
      user.setName(newName); // Update the user object
    } catch (IllegalArgumentException e) {
      System.out.println("Failed to update name: " + e.getMessage());
    }
  }

//  private void updateEmail() throws IOException {
//    System.out.print("Enter new email: ");
//    String newEmail = getValidEmail();
//    try {
//      userController.updateUserEmail(currentUserEmail, newEmail);
//      System.out.println("Email updated successfully.");
//      currentUserEmail = newEmail;
//    } catch (IllegalArgumentException e) {
//      System.out.println("Failed to update email: " + e.getMessage());
//    }
//  }
//
//  private void updateName() throws IOException {
//    System.out.print("Enter new name: ");
//    String newName = scanner.nextLine().trim();
//    try {
//      userController.updateUserName(currentUserEmail, newName);
//      System.out.println("Name updated successfully.");
//    } catch (IllegalArgumentException e) {
//      System.out.println("Failed to update name: " + e.getMessage());
//    }
//  }

//  private void updatePassword() throws IOException {
//    System.out.print("Enter new password: ");
//    String newPassword = scanner.nextLine().trim();
//    try {
//      userController.updateUserPassword(currentUserEmail, newPassword);
//      System.out.println("Password updated successfully.");
//    } catch (IllegalArgumentException e) {
//      System.out.println("Failed to update password: " + e.getMessage());
//    }
//  }
//
//  private boolean deleteProfile() {
//    System.out.println("\nAre you sure you want to delete your profile? This action cannot be undone.");
//    System.out.print("Type 'YES' to confirm: ");
//    String confirmation = scanner.nextLine().trim();
//
//    if (confirmation.equals("YES")) {
//      try {
//        userController.deleteUserAccount(currentUserEmail);
//        System.out.println("Your profile has been deleted. Goodbye!");
//        logout();
//        return true;
//      } catch (IOException e) {
//        System.out.println("An error occurred while deleting your profile: " + e.getMessage());
//      }
//    } else {
//      System.out.println("Profile deletion cancelled.");
//    }
//    return false;
//  }
  
  private void updatePassword(User user) throws IOException {
    System.out.print("Enter new password: ");
    String newPassword = scanner.nextLine().trim();
    try {
      userController.updateUserPassword(user.getEmail(), newPassword);
      System.out.println("Password updated successfully.");
    } catch (IllegalArgumentException e) {
      System.out.println("Failed to update password: " + e.getMessage());
    }
  }
  
  private boolean deleteProfile(User user) throws IOException {
    System.out.println("\nAre you sure you want to delete your profile? This action cannot be undone.");
    System.out.print("Type 'YES' to confirm: ");
    String confirmation = scanner.nextLine().trim();
    
    if (confirmation.equals("YES")) {
      try {
        userController.deleteUserAccount(user.getEmail());
        System.out.println("Your profile has been deleted. Goodbye!");
        return true; // Profile deleted, return to main menu
      } catch (IOException e) {
        System.out.println("An error occurred while deleting your profile: " + e.getMessage());
      }
    } else {
      System.out.println("Profile deletion cancelled.");
    }
    return false; // Profile not deleted, stay in the current menu
  }

//  private void logout() {
//    currentUserEmail = "";
//  }
  
  private String getValidEmail() {
    while (true) {
      String email = scanner.nextLine().trim();
      if (userController.isValidEmail(email)) {
        return email;
      }
      System.out.println("Invalid email format. Please try again.");
    }
  }

//  private String getValidEmail() {
//    while (true) {
//      String email = scanner.nextLine().trim();
//      try {
//        userController.validateEmail(email);
//        return email;
//      } catch (IllegalArgumentException e) {
//        System.out.println("Invalid email format. Please try again.");
//      }
//    }
//  }
}
