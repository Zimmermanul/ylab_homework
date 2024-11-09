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
 * Web configuration class for Spring MVC application.
 * Configures web-related components including CORS, resource handlers,
 * message converters, exception handling, and OpenAPI documentation.
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
  
  @Value("${application.security.allowed-origins:*}")
  private String allowedOrigins;
  
  @Value("${application.security.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
  private String allowedMethods;
  
  @Value("${application.security.max-age:3600}")
  private long maxAge;
  
  /**
   * Creates a character encoding filter to ensure proper UTF-8 encoding
   * for all requests and responses.
   *
   * @return configured CharacterEncodingFilter instance
   */
  @Bean
  public CharacterEncodingFilter characterEncodingFilter() {
    CharacterEncodingFilter filter = new CharacterEncodingFilter();
    filter.setEncoding("UTF-8");
    filter.setForceEncoding(true);
    return filter;
  }
  
  /**
   * Configures exception resolution mappings for the application.
   * Maps specific exceptions to corresponding error views:
   * - Generic Exception -> error
   * - RuntimeException -> error/500
   * - NoHandlerFoundException -> error/404
   *
   * @return configured SimpleMappingExceptionResolver instance
   */
  @Bean
  public SimpleMappingExceptionResolver exceptionResolver() {
    SimpleMappingExceptionResolver resolver = new SimpleMappingExceptionResolver();
    Properties mappings = new Properties();
    mappings.setProperty("Exception", "error");
    mappings.setProperty("RuntimeException", "error/500");
    mappings.setProperty("org.springframework.web.servlet.NoHandlerFoundException", "error/404");
    resolver.setExceptionMappings(mappings);
    resolver.setDefaultErrorView("error");
    resolver.setExceptionAttribute("exception");
    resolver.setWarnLogCategory("com.mkhabibullin.presentation.exception");
    return resolver;
  }
  
  /**
   * Configures CORS mappings for the application.
   * Settings are loaded from application properties:
   * - Allowed origins from application.security.allowed-origins
   * - Allowed methods from application.security.allowed-methods
   * - Max age from application.security.max-age
   *
   * @param registry the CorsRegistry to configure
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
   * Configures resource handlers for static resources.
   * Sets up handlers for:
   * - Swagger UI resources
   * - Webjars resources
   * - Static resources under /static/
   * - Additional resources under /resources/
   *
   * @param registry the ResourceHandlerRegistry to configure
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/swagger-ui/**")
      .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/");
    registry.addResourceHandler("/webjars/**")
      .addResourceLocations("classpath:/META-INF/resources/webjars/");
    registry.addResourceHandler("/resources/**")
      .addResourceLocations("/resources/", "classpath:/resources/");
    if (this.getClass().getClassLoader().getResource("static") != null) {
      registry.addResourceHandler("/static/**")
        .addResourceLocations("classpath:/static/")
        .resourceChain(false);
    }
  }
  
  /**
   * Configures view resolvers for the application.
   * @param registry ViewResolverRegistry to configure
   */
  @Override
  public void configureViewResolvers(ViewResolverRegistry registry) {
    registry.jsp("/WEB-INF/views/", ".jsp");
  }
  
  /**
   * Configures view controllers for the application.
   * Sets up:
   * - Redirect to Swagger UI from root and /swagger-ui
   * - Error page mappings (404, 500, generic error)
   *
   * @param registry the ViewControllerRegistry to configure
   */
  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry.addRedirectViewController("/swagger-ui", "/swagger-ui/index.html");
    registry.addRedirectViewController("/", "/swagger-ui/index.html");
  }
  
  /**
   * Configures HTTP message converters for handling request/response body conversions.
   * Adds:
   * - ByteArrayHttpMessageConverter for byte array handling
   * - MappingJackson2HttpMessageConverter for JSON processing
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
   * Groups all API endpoints under /api/** path.
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
   * Includes:
   * - API title and version
   * - Description
   * - Contact information
   * - Server configurations
   *
   * @return configured OpenAPI instance
   */
  @Bean
  public OpenAPI customOpenAPI() {
    return new OpenAPI()
      .info(new Info()
        .title("Habit Tracker API")
        .version("1.0.0")
        .description("Spring MVC application for tracking habits and personal development")
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
   * Configures:
   * - Java 8 date/time module
   * - Disables writing dates as timestamps
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