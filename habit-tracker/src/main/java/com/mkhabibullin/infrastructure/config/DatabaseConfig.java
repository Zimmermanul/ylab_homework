package com.mkhabibullin.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import liquibase.integration.spring.SpringLiquibase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Configuration class for database-related setup.
 * Configures the data source, entity manager factory, and transaction manager
 * for the application's database connectivity.
 */
@Configuration
@EnableTransactionManagement
public class
DatabaseConfig {
  private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
  
  /**
   * Creates and configures the application's data source using HikariCP.
   * Configuration values are read from environment variables with defaults provided.
   *
   * @return configured HikariDataSource instance
   */
  @Bean(name = "dataSource")
  @Primary
  public DataSource dataSource() {
    HikariConfig config = new HikariConfig();
    String host = System.getenv().getOrDefault("DB_HOST", "postgres");
    String port = System.getenv().getOrDefault("DB_PORT", "5432");
    String dbName = System.getenv().getOrDefault("DB_NAME", "habit-tracker-db");
    String username = System.getenv().getOrDefault("DB_USER", "habit-tracker-admin");
    String password = System.getenv().getOrDefault("DB_PASSWORD", "habittrackerpass123");
    String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, dbName);
    logger.info("Configuring DataSource with URL: {}", jdbcUrl);
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
  
  @Bean
  public SpringLiquibase liquibase(DataSource dataSource) {
    SpringLiquibase liquibase = new SpringLiquibase();
    liquibase.setDataSource(dataSource);
    liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.xml");
    liquibase.setDefaultSchema("public");
    liquibase.setContexts("default");
    liquibase.setDropFirst(false);
    return liquibase;
  }
  
  /**
   * Creates and configures the JPA EntityManagerFactory.
   * Sets up Hibernate as the JPA provider with specific configuration properties.
   *
   * @return configured EntityManagerFactory
   */
  @Bean
  @DependsOn("liquibase")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dataSource());
    em.setPackagesToScan("com.mkhabibullin.domain.model");
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setShowSql(true);
    vendorAdapter.setGenerateDdl(false);
    em.setJpaVendorAdapter(vendorAdapter);
    Properties props = new Properties();
    props.setProperty("hibernate.hbm2ddl.auto", "none");
    props.setProperty("hibernate.physical_naming_strategy",
      "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
    props.setProperty("hibernate.implicit_naming_strategy",
      "org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl");
    props.setProperty("hibernate.format_sql", "true");
    em.setJpaProperties(props);
    return em;
  }
  
  /**
   * Creates and configures the transaction manager.
   * Uses JPA-based transaction management for the application.
   *
   * @param emf the entity manager factory to use for transactions
   * @return configured PlatformTransactionManager
   */
  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
    return new JpaTransactionManager(emf);
  }
}