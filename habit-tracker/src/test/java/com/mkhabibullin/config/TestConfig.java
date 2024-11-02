package com.mkhabibullin.config;


import com.mkhabibullin.infrastructure.config.AspectConfig;
import com.mkhabibullin.infrastructure.config.DatabaseConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.TestPropertySource;

@Configuration
@Profile("test")
@Import({AspectConfig.class, DatabaseConfig.class})
@ComponentScan(basePackages = {
  "com.mkhabibullin.aspect",
  "com.mkhabibullin.infrastructure.persistence.repository"
})
@TestPropertySource(locations = "classpath:application-test.yml")
public class TestConfig {
}