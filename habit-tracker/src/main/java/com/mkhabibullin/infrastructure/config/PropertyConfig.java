package com.mkhabibullin.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

/**
 * Configuration class for property sources handling.
 * Sets up the configuration for loading and managing application properties.
 */
@Configuration
public class PropertyConfig {
  
  /**
   * Creates and configures the PropertySourcesPlaceholderConfigurer.
   * This bean is responsible for resolving ${...} placeholders within Spring bean definitions
   * using properties from application.yml.
   *
   * @return configured PropertySourcesPlaceholderConfigurer instance
   * @throws IllegalStateException if the application.yml resource cannot be found
   *                               or if there are unresolvable placeholders
   */
  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
    configurer.setLocation(new ClassPathResource("application.yml"));
    configurer.setIgnoreResourceNotFound(false);
    configurer.setIgnoreUnresolvablePlaceholders(false);
    return configurer;
  }
}