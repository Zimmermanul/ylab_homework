package com.mkhabibullin.infrastructure.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

/**
 * Configuration class for Liquibase database migration setup.
 * Configures and initializes Liquibase for managing database schema changes.
 */
@Configuration
public class LiquibaseConfig {
  
  /**
   * Creates and configures the Liquibase bean for database migrations.
   * This bean depends on the dataSource being initialized first.
   *
   * @param dataSource the configured data source to use for migrations
   * @return configured SpringLiquibase instance
   */
  @Bean
  @DependsOn("dataSource")
  public SpringLiquibase liquibase(DataSource dataSource) {
    SpringLiquibase liquibase = new SpringLiquibase();
    liquibase.setDataSource(dataSource);
    liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.xml");
    liquibase.setDefaultSchema("public");
    return liquibase;
  }
}