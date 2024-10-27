package com.mkhabibullin;

import com.mkhabibullin.app.presentation.MainMenuConsoleInterface;
import com.mkhabibullin.app.util.DataSourceConfig;
import com.mkhabibullin.app.util.LiquibaseMigrationConfig;

import javax.sql.DataSource;

/**
 * Main application class that serves as the entry point for the application.
 * This class initializes necessary components and starts the application.
 */
public class App {
  private final DataSource dataSource;
  private final LiquibaseMigrationConfig liquibaseMigrationConfig;
  
  /**
   * Constructs a new Habit Tracker App instance.
   */
  public App() {
    this.dataSource = DataSourceConfig.getDataSource();
    this.liquibaseMigrationConfig = new LiquibaseMigrationConfig(dataSource);
  }
  
  /**
   * Runs database migrations manually.
   */
  public void updateDatabase() {
    System.out.println("Running database migrations...");
    liquibaseMigrationConfig.updateDB();
    System.out.println("Database migrations completed successfully");
  }
  
  /**
   * Starts the console interface for development testing.
   */
  public void startConsole() {
    System.out.println("Starting application in console mode");
    updateDatabase();
    MainMenuConsoleInterface mainMenu = new MainMenuConsoleInterface(dataSource);
    mainMenu.start();
  }
  
  /**
   * The main method that serves as the entry point for development tasks.
   * Usage:
   * --migrate: only run database migrations
   * --console: start in console mode (includes migrations)
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    App app = new App();
    
    if (args.length > 0) {
      switch (args[0]) {
        case "--migrate" -> {
          System.out.println("Running database migrations only");
          app.updateDatabase();
        }
        case "--console" -> {
          System.out.println("Starting in console mode");
          app.startConsole();
        }
        default -> {
          System.out.println("Unknown command line argument:\n" + args[0]);
          printUsage();
        }
      }
    } else {
      System.out.println("No command line argument provided");
      printUsage();
    }
  }
  
  private static void printUsage() {
    System.out.println("""
      Usage:
      java -jar habit-tracker.jar --migrate  : Run database migrations
      java -jar habit-tracker.jar --console  : Start in console mode
      
      Note: For web deployment, use Tomcat instead of this main class.
      """);
  }
}