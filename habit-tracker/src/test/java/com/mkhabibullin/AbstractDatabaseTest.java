package com.mkhabibullin;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Testcontainers
public abstract class AbstractDatabaseTest {
  @Container
  protected static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
    .withDatabaseName("testdb")
    .withUsername("test")
    .withPassword("test");
  
  protected static HikariDataSource dataSource;
  
  @BeforeAll
  static void beforeAll() {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(postgres.getJdbcUrl());
    config.setUsername(postgres.getUsername());
    config.setPassword(postgres.getPassword());
    config.setMaximumPoolSize(5);
    config.setMinimumIdle(1);
    
    dataSource = new HikariDataSource(config);
  }
  
  @AfterAll
  static void afterAll() {
    if (dataSource != null) {
      dataSource.close();
    }
  }
  
  @BeforeEach
  void setUp() throws SQLException {
    try (Connection conn = dataSource.getConnection();
         Statement stmt = conn.createStatement()) {
      stmt.execute("CREATE SCHEMA IF NOT EXISTS entity");
      stmt.execute("CREATE SCHEMA IF NOT EXISTS audit");
      stmt.execute("CREATE SEQUENCE IF NOT EXISTS entity.global_seq START 100000");
      stmt.execute("""
                CREATE TABLE IF NOT EXISTS entity.users (
                    id BIGINT DEFAULT nextval('entity.global_seq') PRIMARY KEY,
                    email VARCHAR(255) NOT NULL UNIQUE,
                    password_hash VARCHAR(255) NOT NULL,
                    salt VARCHAR(255) NOT NULL,
                    name VARCHAR(255) NOT NULL,
                    is_admin BOOLEAN DEFAULT FALSE,
                    is_blocked BOOLEAN DEFAULT FALSE
                )
            """);
      
      stmt.execute("""
                CREATE TABLE IF NOT EXISTS entity.habits (
                    id BIGINT DEFAULT nextval('entity.global_seq') PRIMARY KEY,
                    user_id BIGINT REFERENCES entity.users(id),
                    name VARCHAR(255) NOT NULL,
                    description TEXT,
                    frequency VARCHAR(10) NOT NULL,
                    creation_date DATE NOT NULL,
                    is_active BOOLEAN DEFAULT TRUE
                )
            """);
      
      stmt.execute("""
                CREATE TABLE IF NOT EXISTS entity.habit_executions (
                    id BIGINT DEFAULT nextval('entity.global_seq') PRIMARY KEY,
                    habit_id BIGINT REFERENCES entity.habits(id),
                    date DATE NOT NULL,
                    completed BOOLEAN NOT NULL
                )
            """);
      
      stmt.execute("""
            CREATE TABLE IF NOT EXISTS audit.audit_logs (
                id BIGINT DEFAULT nextval('entity.global_seq') PRIMARY KEY,
                username VARCHAR(255) NOT NULL,
                method_name VARCHAR(255) NOT NULL,
                operation TEXT,
                timestamp TIMESTAMP NOT NULL,
                execution_time_ms BIGINT NOT NULL,
                request_uri TEXT,
                request_method VARCHAR(10)
            )
        """);
      
      stmt.execute("TRUNCATE entity.habit_executions CASCADE");
      stmt.execute("TRUNCATE entity.habits CASCADE");
      stmt.execute("TRUNCATE entity.users CASCADE");
      stmt.execute("TRUNCATE audit.audit_logs CASCADE");
    }
  }
}