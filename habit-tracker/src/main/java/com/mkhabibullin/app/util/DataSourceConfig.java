package com.mkhabibullin.app.util;

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
 */
public class DataSourceConfig {
  private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);
  private static volatile HikariDataSource dataSource;
  
  static {
    try {
      // Explicitly register PostgreSQL driver
      DriverManager.registerDriver(new Driver());
      logger.info("PostgreSQL driver registered successfully");
    } catch (SQLException e) {
      logger.error("Failed to register PostgreSQL driver", e);
      throw new RuntimeException("Failed to register PostgreSQL driver", e);
    }
  }
  
  private DataSourceConfig() {
  }
  
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