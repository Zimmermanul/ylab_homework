package com.mkhabibullin.infrastructure.config;

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
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Web configuration class for the application.
 * Configures web-related components including CORS, resource handlers,
 * message converters, and OpenAPI documentation.
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {
  "com.mkhabibullin.presentation.controller",
  "com.mkhabibullin.application.mapper",
  "com.mkhabibullin.application.validation",
  "org.springdoc"
})
public class WebConfig implements WebMvcConfigurer {
  
  @Value("${server.port:18080}")
  private String serverPort;
  
  /**
   * Configures CORS mappings for the application.
   * Allows cross-origin requests from any origin with standard HTTP methods.
   *
   * @param registry the CORS registry to configure
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
      .allowedOrigins("*")
      .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
      .allowedHeaders("*")
      .maxAge(3600);
  }
  
  /**
   * Configures resource handlers for Swagger UI and related resources.
   *
   * @param registry the resource handler registry to configure
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/swagger-ui/**")
      .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/");
    registry.addResourceHandler("/webjars/**")
      .addResourceLocations("classpath:/META-INF/resources/webjars/");
  }
  
  /**
   * Configures view controllers for redirecting to Swagger UI.
   *
   * @param registry the view controller registry to configure
   */
  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addRedirectViewController("/swagger-ui", "/swagger-ui/index.html");
    registry.addRedirectViewController("/", "/swagger-ui/index.html");
  }
  
  /**
   * Configures HTTP message converters for handling request/response body conversions.
   *
   * @param converters the list of converters to configure
   */
  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(new ByteArrayHttpMessageConverter());
    converters.add(new MappingJackson2HttpMessageConverter(objectMapper()));
  }
  
  /**
   * Creates a grouped OpenAPI configuration for public endpoints.
   *
   * @return configured GroupedOpenApi instance
   */
  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
      .group("habit-tracker-public")
      .pathsToMatch("/api/**")
      .build();
  }
  
  /**
   * Creates a custom OpenAPI configuration with detailed API information.
   *
   * @return configured OpenAPI instance
   */
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
      .info(new Info()
        .title("Habit Tracker API")
        .version("1.0.0")
        .description("Spring application for tracking habits and personal development")
        .contact(new Contact()
          .name("Your Name")
          .email("your.email@example.com")))
      .servers(List.of(
        new Server()
          .url("http://localhost:" + serverPort)
          .description("Local server")
      ));
  }
  
  /**
   * Creates and configures an ObjectMapper for JSON serialization/deserialization.
   * Configures Java 8 date/time module and disables timestamp writing for dates.
   *
   * @return configured ObjectMapper instance
   */
  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    return mapper;
  }
}