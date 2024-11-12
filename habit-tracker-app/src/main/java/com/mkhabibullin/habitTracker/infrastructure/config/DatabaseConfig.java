package com.mkhabibullin.habitTracker.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
/**
 * Configuration class for database-related setup in Spring Boot.
 * Configures the data source with HikariCP while leveraging Spring Boot's auto-configuration.
 */
@Configuration
@EnableTransactionManagement
@Slf4j
public class DatabaseConfig {
  
  /**
   * Creates DataSourceProperties to handle basic database connection properties.
   */
  @Bean
  @Primary
  @ConfigurationProperties("spring.datasource")
  public DataSourceProperties dataSourceProperties() {
    return new DataSourceProperties();
  }
  
  /**
   * Creates and configures the application's data source using HikariCP.
   * Configuration values are read from application.yml with environment variable fallbacks.
   */
  @Bean(name = "dataSource")
  @Primary
  @ConfigurationProperties(prefix = "spring.datasource.hikari")
  public DataSource dataSource(DataSourceProperties properties) {
    HikariConfig config = new HikariConfig();
    String host = System.getenv().getOrDefault("DB_HOST", "postgres");
    String port = System.getenv().getOrDefault("DB_PORT", "5432");
    String dbName = System.getenv().getOrDefault("DB_NAME", "habit-tracker-db");
    String username = System.getenv().getOrDefault("DB_USER", "habit-tracker-admin");
    String password = System.getenv().getOrDefault("DB_PASSWORD", "habittrackerpass123");
    String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);
    log.info("Configuring DataSource with URL: {}", jdbcUrl);
    config.setJdbcUrl(jdbcUrl);
    config.setUsername(username);
    config.setPassword(password);
    config.setDriverClassName("org.postgresql.Driver");
    config.setMaximumPoolSize(10);
    config.setMinimumIdle(5);
    config.setIdleTimeout(300000);
    config.setConnectionTimeout(20000);
    config.setValidationTimeout(5000);
    config.setPoolName("HabitTrackerPool");
    return new HikariDataSource(config);
  }
}