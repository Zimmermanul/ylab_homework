package com.mkhabibullin.swagger.annotation;

import com.mkhabibullin.swagger.config.SwaggerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Enables Swagger/OpenAPI documentation for the application.
 * Add this annotation to your configuration to enable Swagger UI and API documentation.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SwaggerConfiguration.class)
public @interface EnableSwaggerDoc {
}