package com.mkhabibullin.app;

import com.mkhabibullin.app.util.ApplicationConfig;
import com.mkhabibullin.app.util.ConfigLoader;
import com.mkhabibullin.app.util.DataSourceConfig;
import com.mkhabibullin.app.util.LiquibaseMigrationConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * ServletContextListener that initializes the application when deployed to a web container.
 * Handles database initialization, migrations, and application component setup.
 */
@WebListener
public class ApplicationInitializer implements ServletContextListener {
  private static final Logger logger = LoggerFactory.getLogger(ApplicationInitializer.class);
  private DataSource dataSource;
  private ApplicationConfig applicationConfig;
  
  @Override
  public void contextInitialized(ServletContextEvent event) {
    try {
      logger.info("Initializing Habit Tracker application...");
      initializeDataSource();
      runDatabaseMigrations();
      initializeApplicationComponents(event.getServletContext());
      logger.info("Application initialized successfully");
    } catch (Exception e) {
      logger.error("Failed to initialize application", e);
      throw new RuntimeException("Failed to initialize application", e);
    }
  }
  
  @Override
  public void contextDestroyed(ServletContextEvent event) {
    try {
      logger.info("Shutting down application...");
      cleanupResources();
      logger.info("Application shutdown completed successfully");
    } catch (Exception e) {
      logger.error("Error during application shutdown", e);
    }
  }
  
  private void initializeDataSource() {
    try {
      logger.info("Initializing DataSource...");
      this.dataSource = DataSourceConfig.getDataSource();
      logger.info("DataSource initialized successfully");
    } catch (Exception e) {
      logger.error("Failed to initialize DataSource", e);
      throw new RuntimeException("Failed to initialize DataSource", e);
    }
  }
  
  private void runDatabaseMigrations() {
    try {
      if (!ConfigLoader.isLiquibaseEnabled()) {
        logger.info("Database migrations are disabled, skipping...");
        return;
      }
      
      logger.info("Running database migrations...");
      LiquibaseMigrationConfig liquibaseMigrationConfig = new LiquibaseMigrationConfig(dataSource);
      liquibaseMigrationConfig.updateDB();
      logger.info("Database migrations completed successfully");
    } catch (Exception e) {
      logger.error("Failed to run database migrations", e);
      throw new RuntimeException("Failed to run database migrations", e);
    }
  }
  
  private void initializeApplicationComponents(ServletContext context) {
    try {
      logger.info("Initializing application components...");
      
      // Initialize application config
      this.applicationConfig = new ApplicationConfig(dataSource);
      
      // Register controllers in servlet context
      registerControllers(context);
      
      logger.info("Application components initialized successfully");
    } catch (Exception e) {
      logger.error("Failed to initialize application components", e);
      throw new RuntimeException("Failed to initialize application components", e);
    }
  }
  
  private void registerControllers(ServletContext context) {
    if (context == null) {
      throw new IllegalArgumentException("ServletContext cannot be null");
    }
    
    logger.debug("Registering controllers in servlet context...");
    
    // Register each controller
    context.setAttribute("userController", applicationConfig.getUserController());
    context.setAttribute("habitController", applicationConfig.getHabitController());
    context.setAttribute("executionController", applicationConfig.getExecutionController());
    
    logger.debug("Controllers registered successfully");
  }
  
  private void cleanupResources() {
    try {
      logger.info("Cleaning up application resources...");
      
      // Close DataSource
      if (dataSource != null) {
        DataSourceConfig.closeDataSource();
        logger.info("DataSource closed successfully");
      }
      
      // Clear references
      dataSource = null;
      applicationConfig = null;
      
      logger.info("Resource cleanup completed");
    } catch (Exception e) {
      logger.error("Error during resource cleanup", e);
      throw new RuntimeException("Failed to clean up resources", e);
    }
  }
}