package com.mkhabibullin.app.util;

import com.zaxxer.hikari.HikariConfig;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class provides methods to manage database operations such as loading
 * configuration properties, creating HikariCP configurations, and managing Liquibase database
 * migrations.
 */
public class DatabaseManager {
  private static final String LIQUIBASE_SERVICE_SCHEMA = "service";
  
  
  public static HikariConfig createHikariConfig() {
    
    final String jdbc_url = ConfigLoader.getDatabaseUrl();
    final String user = ConfigLoader.getDatabaseUser();
    final String password =ConfigLoader.getDatabasePassword();
    
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(jdbc_url);
    config.setUsername(user);
    config.setPassword(password);
    return config;
  }
  
  /**
   * Creates the Liquibase service schema if it does not already exist.
   *
   * @param connection the {@link java.sql.Connection} to the database
   */
  public static void createLiquibaseServiceSchema(Connection connection) {
    try {
      Statement statement = connection.createStatement();
      statement.execute("CREATE SCHEMA IF NOT EXISTS " + LIQUIBASE_SERVICE_SCHEMA);
    } catch (SQLException e) {
      System.out.println("Exception while creating a schema " + LIQUIBASE_SERVICE_SCHEMA + ":\n " + e.getMessage());
    }
  }
  
  /**
   * Creates and returns a {@link liquibase.Liquibase} instance using the provided connection.
   * <p>
   * This method sets up the Liquibase environment using the changelog file.
   *
   * @param connection the {@link java.sql.Connection} to be used by Liquibase
   * @return a configured {@link liquibase.Liquibase} instance
   */
  public static Liquibase createLiquibase(Connection connection) {
    Database database = createDatabase(connection);
    return new Liquibase(ConfigLoader.getLiquibaseChangeLogFile(), new ClassLoaderResourceAccessor(), database);
  }
  
  /**
   * Creates and returns a {@link liquibase.database.Database} instance using the provided connection.
   * <p>
   * This method initializes a {@link liquibase.database.jvm.JdbcConnection} and sets
   * the Liquibase schema name for the connection.
   *
   * @param connection the {@link java.sql.Connection} to be used by the database
   * @return a {@link liquibase.database.Database} object or {@code null} if an exception occurs
   */
  private static Database createDatabase(Connection connection) {
    Database database = null;
    try {
      database = DatabaseFactory.getInstance()
        .findCorrectDatabaseImplementation(new JdbcConnection(connection));
      database.setLiquibaseSchemaName(LIQUIBASE_SERVICE_SCHEMA);
    } catch (DatabaseException e) {
      System.out.println("Exception while creating a database: \n " + e.getMessage());
    }
    
    return database;
  }
}