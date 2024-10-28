package com.mkhabibullin;

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
   * The main method that serves as the entry point for development tasks.
   * Usage:
   * --migrate: only run database migrations
   * --console: start in console mode (includes migrations)
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    App app = new App();
  }
}