package com.mkhabibullin.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * The {@code DataSourceConfig} class provides utility methods for configuring and
 * obtaining a {@link javax.sql.DataSource} using the HikariCP connection pool.
 * This class implements a thread-safe singleton pattern for managing database connections.
 *
 * <p>The configuration includes:</p>
 * <ul>
 *   <li>Automatic PostgreSQL driver registration</li>
 *   <li>Lazy initialization of the connection pool</li>
 *   <li>Thread-safe access to the data source</li>
 * </ul>
 */
public class DataSourceConfig {
  private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);
  private static volatile HikariDataSource dataSource;
  
  static {
    try {
      DriverManager.registerDriver(new Driver());
      logger.info("PostgreSQL driver registered successfully");
    } catch (SQLException e) {
      logger.error("Failed to register PostgreSQL driver", e);
      throw new RuntimeException("Failed to register PostgreSQL driver", e);
    }
  }
  
  private DataSourceConfig() {
  }
  
  /**
   * Returns the singleton instance of {@link javax.sql.DataSource}.
   * If the data source hasn't been initialized, this method will create it
   * using double-checked locking to ensure thread safety.
   *
   * <p>The data source is configured using HikariCP connection pool with
   * settings from {@link DatabaseManager}.</p>
   *
   * @return A configured {@link javax.sql.DataSource} instance
   */
  public static DataSource getDataSource() {
    if (dataSource == null) {
      synchronized (DataSourceConfig.class) {
        if (dataSource == null) {
          initializeDataSource();
        }
      }
    }
    return dataSource;
  }
  
  private static void initializeDataSource() {
    try {
      logger.info("Initializing HikariCP data source");
      HikariConfig config = DatabaseManager.createHikariConfig();
      dataSource = new HikariDataSource(config);
      logger.info("HikariCP data source initialized successfully");
    } catch (Exception e) {
      logger.error("Failed to initialize data source", e);
      throw new RuntimeException("Failed to initialize data source", e);
    }
  }
  
  /**
   * Closes the data source and releases all database connections.
   */
  public static void closeDataSource() {
    if (dataSource != null) {
      try {
        logger.info("Closing data source");
        dataSource.close();
        dataSource = null;
        logger.info("Data source closed successfully");
      } catch (Exception e) {
        logger.error("Error closing data source", e);
      }
    }
  }
}