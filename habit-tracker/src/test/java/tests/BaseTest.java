package tests;

import com.mkhabibullin.config.TestConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;
import java.util.List;

@SpringJUnitConfig(TestConfig.class)
@ActiveProfiles("test")
public abstract class BaseTest {
  private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
  protected static final PostgreSQLContainer<?> postgres;
  
  @Autowired
  protected DataSource dataSource;
  
  static {
    postgres = new PostgreSQLContainer<>("postgres:13")
      .withDatabaseName("testdb")
      .withUsername("test")
      .withPassword("test")
      .withReuse(true);
  }
  
  @BeforeAll
  static void startContainer() {
    logger.info("Starting PostgreSQL container");
    postgres.start();
    
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      if (postgres != null && postgres.isRunning()) {
        logger.info("Stopping PostgreSQL container");
        postgres.stop();
      }
    }));
  }
  
  @AfterAll
  static void stopContainer() {
    if (postgres != null && postgres.isRunning()) {
      logger.info("Stopping PostgreSQL container");
      postgres.stop();
    }
  }
  
  @BeforeEach
  void setUp() {
    logger.info("Setting up test database");
    cleanDatabase();
  }
  
  @AfterEach
  void tearDown() {
    logger.info("Cleaning up test database");
    cleanDatabase();
  }
  
  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }
  
  protected void cleanDatabase() {
    try {
      JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
      jdbcTemplate.execute("SET CONSTRAINTS ALL DEFERRED");
      List<String> tableNames = jdbcTemplate.queryForList(
        "SELECT table_name FROM information_schema.tables " +
        "WHERE table_schema = 'public' " +
        "AND table_name NOT IN ('databasechangelog', 'databasechangeloglock')",
        String.class
      );
      for (String tableName : tableNames) {
        logger.debug("Truncating table: {}", tableName);
        jdbcTemplate.execute("TRUNCATE TABLE " + tableName + " CASCADE");
      }
      jdbcTemplate.execute("SET CONSTRAINTS ALL IMMEDIATE");
      
    } catch (Exception e) {
      logger.error("Error cleaning database", e);
      throw new RuntimeException("Failed to clean database", e);
    }
  }
  
  protected void cleanTable(String tableName) {
    try {
      logger.debug("Truncating specific table: {}", tableName);
      JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
      jdbcTemplate.execute("TRUNCATE TABLE " + tableName + " CASCADE");
    } catch (Exception e) {
      logger.error("Error cleaning table: " + tableName, e);
      throw new RuntimeException("Failed to clean table: " + tableName, e);
    }
  }
  
  protected boolean tableExists(String tableName) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    Integer count = jdbcTemplate.queryForObject(
      "SELECT COUNT(*) FROM information_schema.tables " +
      "WHERE table_schema = 'public' AND table_name = ?",
      Integer.class,
      tableName.toLowerCase()
    );
    return count != null && count > 0;
  }
  
  protected int getTableRowCount(String tableName) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
    return jdbcTemplate.queryForObject(
      "SELECT COUNT(*) FROM " + tableName,
      Integer.class
    );
  }
}