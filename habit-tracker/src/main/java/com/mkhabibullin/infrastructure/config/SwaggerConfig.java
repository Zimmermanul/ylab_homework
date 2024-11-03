package com.mkhabibullin.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
  info = @Info(
    title = "Habit Tracker API",
    version = "1.0.0",
    description = "Spring application for tracking habits and personal development"
  ),
  servers = {
    @Server(
      url = "/",
      description = "Default Server URL"
    )
  }
)
public class SwaggerConfig {
}