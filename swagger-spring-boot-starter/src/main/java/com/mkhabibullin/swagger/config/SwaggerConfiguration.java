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
 */
@Configuration
@EnableConfigurationProperties(SwaggerProperties.class)
@PropertySource("classpath:swagger-defaults.properties")
public class SwaggerConfiguration {
  
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