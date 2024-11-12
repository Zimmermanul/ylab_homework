package com.mkhabibullin.habitTracker;

import com.mkhabibullin.habitTracker.infrastructure.config.DatabaseConfig;
import com.mkhabibullin.habitTracker.infrastructure.config.WebConfig;
import com.mkhabibullin.swagger.annotation.EnableSwaggerDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * Main Spring Boot application class that initializes the application context
 * and configures the Spring MVC infrastructure.
 */
@SpringBootApplication(scanBasePackages = {
  "com.mkhabibullin.habitTracker.application.service",
  "com.mkhabibullin.habitTracker.infrastructure.persistence.repository",
  "com.mkhabibullin.habitTracker.application.mapper",
  "com.mkhabibullin.habitTracker.application.validation"
})
@Import({
  DatabaseConfig.class,
  WebConfig.class
})
@EnableSwaggerDoc
public class HabitTrackerApplication {
  
  public static void main(String[] args) {
    SpringApplication.run(HabitTrackerApplication.class, args);
  }
}