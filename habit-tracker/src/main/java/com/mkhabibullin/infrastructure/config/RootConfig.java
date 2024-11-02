package com.mkhabibullin.infrastructure.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {
  "com.mkhabibullin.application.service",
  "com.mkhabibullin.infrastructure.persistence.repository"
})
@Import({
  DatabaseConfig.class,
  LiquibaseConfig.class,
  AspectConfig.class
})
public class RootConfig {
}
