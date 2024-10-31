package com.mkhabibullin.infrastructure.config;

import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static com.mkhabibullin.infrastructure.config.DatabaseManager.createLiquibase;
import static com.mkhabibullin.infrastructure.config.DatabaseManager.createLiquibaseServiceSchema;

/**
 * The {@code LiquibaseMigrationConfig} is responsible for managing database
 * migrations using Liquibase. It holds a {@link javax.sql.DataSource} and provides
 * functionality to update the database schema.
 * <p>
 * This class simplifies the process of performing Liquibase migrations by establishing
 * a database connection and executing Liquibase update scripts.
 *
 * @param dataSource the {@link javax.sql.DataSource} used to connect to the database for migration
 */
public record LiquibaseMigrationConfig(DataSource dataSource) {
  public void updateDB() {
    try (Connection connection = dataSource.getConnection()) {
      createLiquibaseServiceSchema(connection);
      Liquibase liquibase = createLiquibase(connection);
      liquibase.update();
    } catch (SQLException | LiquibaseException e) {
      System.out.println("Exception in migration: " + e.getMessage());
    }
  }
}