package com.mkhabibullin.infrastructure.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Root configuration class for the application.
 * Serves as the main configuration entry point, combining component scanning
 * and importing other configuration classes.
 */
@Configuration
@ComponentScan(basePackages = {
  "com.mkhabibullin.application.service",
  "com.mkhabibullin.infrastructure.persistence.repository",
  "com.mkhabibullin.application.mapper",
  "com.mkhabibullin.application.validation"
})
@Import({
  PropertyConfig.class,
  AspectConfig.class,
  DatabaseConfig.class,
  LiquibaseConfig.class,
  WebConfig.class
})
public class RootConfig {
}
