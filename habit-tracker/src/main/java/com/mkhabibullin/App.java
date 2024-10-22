package com.mkhabibullin;

import com.mkhabibullin.app.data.UserDbRepository;
import com.mkhabibullin.app.presentation.MainMenuConsoleInterface;
import com.mkhabibullin.app.service.UserService;
import com.mkhabibullin.app.util.DataSourceConfig;
import com.mkhabibullin.app.util.LiquibaseMigrationConfig;

import javax.sql.DataSource;

/**
 * Main application class that serves as the entry point for the application.
 * This class initializes necessary components and starts the application.
 */
public class App {
  private DataSource dataSource;
  
  /**
   * Constructs a new Habit Tracker App instance.
   */
  public App() {
    this.dataSource = DataSourceConfig.getDataSource();
  }
  
  /**
   * Initializes and starts the application by:
   * 1. Running database migrations using Liquibase to ensure schema is up to date
   * 2. Launching the main menu console interface
   *
   */
  public void start() {
    new LiquibaseMigrationConfig(dataSource).updateDB();
    new MainMenuConsoleInterface(dataSource).start();
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
