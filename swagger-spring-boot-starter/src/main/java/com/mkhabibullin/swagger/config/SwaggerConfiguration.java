package com.mkhabibullin.swagger.config;

import com.mkhabibullin.swagger.properties.SwaggerProperties;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration class for Swagger/OpenAPI documentation.
 * This class provides the necessary beans for configuring and customizing
 * the OpenAPI documentation for the application.
 *
 * <p>Default properties are loaded from 'swagger-defaults.properties' file,
 * which can be overridden by application-specific configurations.</p>
 *
 * <p>Properties are managed through {@link SwaggerProperties} and can be configured
 * in application.properties/yml using the 'swagger' prefix.</p>
 *
 * @see SwaggerProperties
 * @see EnableConfigurationProperties
 * @see PropertySource
 */
@Configuration
@EnableConfigurationProperties(SwaggerProperties.class)
@PropertySource("classpath:swagger-defaults.properties")
public class SwaggerConfiguration {
  
  /**
   * Creates and configures the primary OpenAPI bean for Swagger documentation.
   * This bean defines the core API information such as title, version, and description.
   *
   * <p>The bean is only created if no other OpenAPI bean exists in the context,
   * allowing for custom overrides in the main application.</p>
   *
   * @param properties the configured Swagger properties
   * @return configured OpenAPI instance with API information
   * @see OpenAPI
   * @see SwaggerProperties
   * @see ConditionalOnMissingBean
   */
  @Bean
  @ConditionalOnMissingBean
  public OpenAPI customOpenAPI(SwaggerProperties properties) {
    return new OpenAPI()
      .info(new Info()
        .title(properties.getTitle())
        .version(properties.getVersion())
        .description(properties.getDescription())
        .termsOfService("http://swagger.io/terms/"));
  }
  
  /**
   * Creates and configures a GroupedOpenApi bean for organizing API endpoints.
   * This bean defines which packages to scan and which paths to include in the API documentation.
   *
   * <p>The bean is only created if no other GroupedOpenApi bean exists in the context,
   * allowing for custom overrides in the main application.</p>
   *
   * <p>Configuration includes:
   * <ul>
   *     <li>Group name for API organization</li>
   *     <li>Package scanning configuration for finding controllers</li>
   *     <li>Path patterns for including specific endpoints</li>
   * </ul>
   * </p>
   *
   * @param properties the configured Swagger properties
   * @return configured GroupedOpenApi instance
   * @see GroupedOpenApi
   * @see SwaggerProperties
   * @see ConditionalOnMissingBean
   */
  @Bean
  @ConditionalOnMissingBean
  public GroupedOpenApi customApi(SwaggerProperties properties) {
    return GroupedOpenApi.builder()
      .group("api")
      .packagesToScan(properties.getBasePackage())
      .pathsToMatch(properties.getPathsToMatch())
      .build();
  }
}