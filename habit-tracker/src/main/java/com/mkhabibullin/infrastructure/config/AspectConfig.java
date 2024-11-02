package com.mkhabibullin.infrastructure.config;

import com.mkhabibullin.aspect.AuditedAspect;
import com.mkhabibullin.infrastructure.persistence.repository.AuditLogRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AspectConfig {
  
  private final Environment environment;
  private final AuditLogRepository auditLogRepository;
  
  public AspectConfig(Environment environment, AuditLogRepository auditLogRepository) {
    this.environment = environment;
    this.auditLogRepository = auditLogRepository;
  }
  
  @Bean
  @Profile("!test")
  public AuditedAspect auditedAspect() {
    return new AuditedAspect(auditLogRepository, environment);
  }
}