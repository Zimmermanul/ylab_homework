package com.mkhabibullin.infrastructure.config;

import com.mkhabibullin.aspect.AuditedAspect;
import com.mkhabibullin.infrastructure.persistence.repository.AuditLogRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

/**
 * Configuration class for aspect setup.
 * Enables and configures aspects for the application.
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AspectConfig {
  private final Environment environment;
  private final AuditLogRepository auditLogRepository;
  
  /**
   * Constructs a new AspectConfig with required dependencies.
   *
   * @param environment        Spring environment for profile detection
   * @param auditLogRepository repository for audit log persistence
   */
  public AspectConfig(Environment environment, AuditLogRepository auditLogRepository) {
    this.environment = environment;
    this.auditLogRepository = auditLogRepository;
  }
  
  /**
   * Creates and configures the AuditedAspect bean.
   * This bean is only active in non-test profiles.
   *
   * @return configured AuditedAspect instance
   */
  @Bean
  @Profile("!test")
  public AuditedAspect auditedAspect() {
    return new AuditedAspect(auditLogRepository, environment);
  }
}