package com.mkhabibullin.audit.config;
import com.mkhabibullin.audit.aspect.AuditedAspect;
import com.mkhabibullin.audit.persistence.repository.AuditLogRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


@Configuration
@ConditionalOnClass(AuditedAspect.class)
@ComponentScan(basePackages = {
  "com.mkhabibullin.persistence.repository",
  "com.mkhabibullin.aspect"
})
public class AuditConfiguration {
  
  @Bean
  @ConditionalOnMissingBean
  public AuditedAspect auditedAspect(AuditLogRepository auditLogRepository, Environment environment) {
    return new AuditedAspect(auditLogRepository, environment);
  }
}