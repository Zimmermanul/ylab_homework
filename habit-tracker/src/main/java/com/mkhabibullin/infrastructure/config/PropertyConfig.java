package com.mkhabibullin.infrastructure.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;

import java.util.Objects;

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
    YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
    yaml.setResources(new ClassPathResource("application.yml"));
    configurer.setProperties(Objects.requireNonNull(yaml.getObject()));
    configurer.setIgnoreResourceNotFound(false);
    configurer.setIgnoreUnresolvablePlaceholders(false);
    configurer.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return configurer;
  }
  
  @Bean
  public static YamlPropertySourceLoader yamlPropertySourceLoader() {
    return new YamlPropertySourceLoader();
  }
}