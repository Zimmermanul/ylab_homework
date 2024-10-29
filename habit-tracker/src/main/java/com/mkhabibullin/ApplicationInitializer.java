package com.mkhabibullin;

import com.mkhabibullin.infrastructure.config.ApplicationConfig;
import com.mkhabibullin.infrastructure.config.ConfigLoader;
import com.mkhabibullin.infrastructure.config.DataSourceConfig;
import com.mkhabibullin.infrastructure.config.LiquibaseMigrationConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * A ServletContextListener implementation that handles the initialization and shutdown
 * of the Habit Tracker web application. This class is responsible for setting up necessary
 * resources when the application starts and cleaning them up when the application shuts down.
 */
@WebListener
public class ApplicationInitializer implements ServletContextListener {
  private static final Logger logger = LoggerFactory.getLogger(ApplicationInitializer.class);
  private DataSource dataSource;
  private ApplicationConfig applicationConfig;
  
  /**
   * Initializes the web application when the server starts up. This method is called by
   * the servlet container when the application context is initialized.
   */
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
  
  /**
   * Performs cleanup when the web application is being shut down. This method is called by
   * the servlet container when the application context is being destroyed.
   */
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
      this.applicationConfig = new ApplicationConfig(dataSource);
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
    context.setAttribute("userController", applicationConfig.getUserController());
    context.setAttribute("habitController", applicationConfig.getHabitController());
    context.setAttribute("executionController", applicationConfig.getExecutionController());
    context.setAttribute("auditLogController", applicationConfig.getAuditLogController());
    logger.debug("Controllers registered successfully");
  }
  
  private void cleanupResources() {
    try {
      logger.info("Cleaning up application resources...");
      if (dataSource != null) {
        DataSourceConfig.closeDataSource();
        logger.info("DataSource closed successfully");
      }
      dataSource = null;
      applicationConfig = null;
      logger.info("Resource cleanup completed");
    } catch (Exception e) {
      logger.error("Error during resource cleanup", e);
      throw new RuntimeException("Failed to clean up resources", e);
    }
  }
}