package com.mkhabibullin.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;
@Configuration
@EnableTransactionManagement
public class DatabaseConfig {
  private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);
  
  @Bean
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
    config.setConnectionTestQuery("SELECT 1");
    return new HikariDataSource(config);
  }
  
  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dataSource);
    em.setPackagesToScan("com.mkhabibullin.domain.model");
    HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    vendorAdapter.setShowSql(true);
    vendorAdapter.setGenerateDdl(false);
    em.setJpaVendorAdapter(vendorAdapter);
    Properties props = new Properties();
    props.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
    props.setProperty("hibernate.format_sql", "true");
    props.setProperty("hibernate.hbm2ddl.auto", "validate");
    em.setJpaProperties(props);
    return em;
  }
  
  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(emf);
    return transactionManager;
  }
}