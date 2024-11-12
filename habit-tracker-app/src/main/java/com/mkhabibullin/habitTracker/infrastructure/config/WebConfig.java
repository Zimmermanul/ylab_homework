package com.mkhabibullin.habitTracker.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import java.util.List;
import java.util.Properties;

/**
 * Web configuration class for Spring Boot application.
 * Configures web-related components while leveraging Spring Boot's auto-configuration.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Value("${server.port:18080}")
  private String serverPort;
  
  @Value("${application.security.allowed-origins:*}")
  private String allowedOrigins;
  
  @Value("${application.security.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
  private String allowedMethods;
  
  @Value("${application.security.max-age:3600}")
  private long maxAge;
  
  /**
   * Configures CORS mappings for the application.
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
      .allowedOrigins(allowedOrigins.split(","))
      .allowedMethods(allowedMethods.split(","))
      .allowedHeaders("*")
      .maxAge(maxAge);
  }
  
  /**
   * Creates a grouped OpenAPI configuration for public endpoints.
   */
  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
      .group("habit-tracker-public")
      .pathsToMatch("/api/**")
      .build();
  }
  
  /**
   * Creates a custom OpenAPI configuration.
   */
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
      .info(new Info()
        .title("Habit Tracker API")
        .version("1.0.0")
        .description("Spring Boot application for tracking habits and personal development")
        .contact(new Contact()
          .name("Your Name")
          .email("your.email@example.com")))
      .servers(List.of(
        new Server()
          .url("http://localhost:" + serverPort)
          .description("Local server")
      ));
  }
}