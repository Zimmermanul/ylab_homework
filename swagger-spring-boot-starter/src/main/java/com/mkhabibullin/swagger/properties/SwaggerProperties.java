package com.mkhabibullin.swagger.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Swagger/OpenAPI documentation.
 */
@Data
@ConfigurationProperties(prefix = "swagger")
public class SwaggerProperties {
  /**
   * API title
   */
  private String title = "REST API";
  
  /**
   * API description
   */
  private String description = "API Documentation";
  
  /**
   * API version
   */
  private String version = "1.0.0";
  
  /**
   * Base package to scan for API controllers
   */
  private String basePackage = "";
  
  /**
   * Path patterns to include
   */
  private String pathsToMatch = "/api/**";
}