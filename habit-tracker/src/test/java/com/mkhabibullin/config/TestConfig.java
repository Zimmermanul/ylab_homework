package com.mkhabibullin.config;


import com.mkhabibullin.infrastructure.config.AspectConfig;
import com.mkhabibullin.infrastructure.config.DatabaseConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * Test environment configuration class.
 * Provides configuration specific to the test environment including:
 * - Aspect-oriented programming setup
 * - Database configuration
 * - Component scanning for repositories and aspects
 * - Test-specific property loading
 * <p>
 * This configuration is only active when the "test" profile is enabled.
 * Imports necessary configurations from AspectConfig and DatabaseConfig while
 * maintaining isolation from production configurations.
 * <p>
 * Component scanning is configured for:
 * - Aspects package: com.mkhabibullin.aspect
 * - Repository package: com.mkhabibullin.infrastructure.persistence.repository
 * <p>
 * Properties are loaded from application-test.yml
 */
@Configuration
@EnableWebMvc
@Profile("test")
@Import({AspectConfig.class, DatabaseConfig.class})
@ComponentScan(basePackages = {
  "com.mkhabibullin.aspect",
  "com.mkhabibullin.infrastructure.persistence.repository"
})
@TestPropertySource(locations = "classpath:application-test.yml")
public class TestConfig {
}