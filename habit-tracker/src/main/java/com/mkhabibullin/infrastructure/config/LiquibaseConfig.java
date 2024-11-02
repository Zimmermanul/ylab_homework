package com.mkhabibullin.infrastructure.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sql.DataSource;

@Configuration
public class LiquibaseConfig {
  
  @Value("${spring.liquibase.change-log}")
  private String changeLogFile;
  
  @Value("${spring.liquibase.default-schema}")
  private String defaultSchema;
  
  @Value("${spring.liquibase.liquibase-schema}")
  private String liquibaseSchema;
  
  @Bean
  @DependsOn("dataSource")
  public SpringLiquibase liquibase(DataSource dataSource) {
    SpringLiquibase liquibase = new SpringLiquibase();
    liquibase.setDataSource(dataSource);
    liquibase.setChangeLog(changeLogFile);
    liquibase.setDefaultSchema(defaultSchema);
    liquibase.setLiquibaseSchema(liquibaseSchema);
    return liquibase;
  }
}