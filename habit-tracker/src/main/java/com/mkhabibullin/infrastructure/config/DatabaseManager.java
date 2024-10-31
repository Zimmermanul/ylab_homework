package com.mkhabibullin.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class provides methods to manage database operations such as loading
 * configuration properties, creating HikariCP configurations, and managing Liquibase database
 * migrations.
 */
public class DatabaseManager {
  private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
  private static final String LIQUIBASE_SERVICE_SCHEMA = "service";
  
  /**
   * Creates and configures a HikariCP configuration object with predefined database
   * connection parameters and pool settings.
   *
   * <p>Configuration includes:</p>
   * <ul>
   *   <li>Connection parameters from {@link ConfigLoader}</li>
   *   <li>Pool size: maximum 10 connections</li>
   *   <li>Minimum idle connections: 5</li>
   *   <li>Idle timeout: 300 seconds</li>
   *   <li>Connection timeout: 20 seconds</li>
   *   <li>Validation timeout: 5 seconds</li>
   * </ul>
   *
   * @return A configured {@link HikariConfig} instance ready for creating a connection pool
   */
  public static HikariConfig createHikariConfig() {
    logger.info("Creating HikariCP configuration");
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(ConfigLoader.getDatabaseUrl());
    config.setUsername(ConfigLoader.getDatabaseUser());
    config.setPassword(ConfigLoader.getDatabasePassword());
    config.setMaximumPoolSize(10);
    config.setMinimumIdle(5);
    config.setIdleTimeout(300000);
    config.setConnectionTimeout(20000);
    config.setValidationTimeout(5000);
    config.setConnectionTestQuery("SELECT 1");
    config.setDriverClassName("org.postgresql.Driver");
    logger.info("HikariCP configuration created successfully");
    return config;
  }
  
  /**
   * Creates the Liquibase service schema if it doesn't exist.
   * This schema is used to store Liquibase metadata and change log information.
   *
   * @param connection An active database connection
   */
  public static void createLiquibaseServiceSchema(Connection connection) {
    try {
      logger.info("Creating Liquibase service schema if not exists");
      try (Statement statement = connection.createStatement()) {
        statement.execute("CREATE SCHEMA IF NOT EXISTS " + LIQUIBASE_SERVICE_SCHEMA);
      }
      logger.info("Liquibase service schema created/verified successfully");
    } catch (SQLException e) {
      logger.error("Failed to create Liquibase service schema", e);
      throw new RuntimeException("Failed to create Liquibase service schema", e);
    }
  }
  
  /**
   * Creates and configures a Liquibase instance for managing database migrations.
   *
   * @param connection An active database connection
   * @return A configured {@link Liquibase} instance ready for running migrations
   */
  public static Liquibase createLiquibase(Connection connection) {
    try {
      logger.info("Creating Liquibase instance");
      Database database = createDatabase(connection);
      if (database == null) {
        throw new RuntimeException("Failed to create database instance for Liquibase");
      }
      
      String changeLogFile = ConfigLoader.getLiquibaseChangeLogFile();
      logger.info("Using changelog file: {}", changeLogFile);
      
      return new Liquibase(
        changeLogFile,
        new ClassLoaderResourceAccessor(),
        database
      );
    } catch (Exception e) {
      logger.error("Failed to create Liquibase instance", e);
      throw new RuntimeException("Failed to create Liquibase instance", e);
    }
  }
  
  private static Database createDatabase(Connection connection) {
    try {
      logger.info("Creating database instance for Liquibase");
      Database database = DatabaseFactory.getInstance()
        .findCorrectDatabaseImplementation(new JdbcConnection(connection));
      database.setLiquibaseSchemaName(LIQUIBASE_SERVICE_SCHEMA);
      return database;
    } catch (Exception e) {
      logger.error("Failed to create database instance", e);
      return null;
    }
  }
}