package com.mkhabibullin.audit.config;
import com.mkhabibullin.audit.aspect.AuditedAspect;
import com.mkhabibullin.audit.persistence.repository.AuditLogRepository;
import com.mkhabibullin.audit.properties.AuditProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 * Configuration class for audit functionality autoconfiguration.
 * This class is responsible for setting up the audit aspect and related components
 * when the required classes are present on the classpath.
 *
 * <p>The configuration is only activated when {@link AuditedAspect} is available
 * on the classpath, as specified by {@link ConditionalOnClass}.</p>
 *
 * <p>Component scanning is configured to automatically detect and register:
 * <ul>
 *     <li>Audit repositories in the persistence package</li>
 *     <li>Audit aspects in the aspect package</li>
 * </ul>
 * </p>
 *
 * @see AuditedAspect
 * @see AuditLogRepository
 * @see ComponentScan
 * @see ConditionalOnClass
 * @see ConditionalOnMissingBean
 */
@Configuration
@ConditionalOnClass(AuditedAspect.class)
@EnableConfigurationProperties(AuditProperties.class)
@ComponentScan(basePackages = {
  "com.mkhabibullin.audit.persistence.repository",
  "com.mkhabibullin.audit.aspect"
})
public class AuditConfiguration {
  
  /**
   * Creates an {@link AuditedAspect} bean if one is not already defined in the context.
   * This aspect handles the auditing of method executions marked with the {@code @Audited} annotation.
   *
   * <p>The aspect is only created if no other bean of type {@link AuditedAspect} exists in the
   * application context, as controlled by {@link ConditionalOnMissingBean}.</p>
   *
   * @return configured instance of {@link AuditedAspect}
   * @see AuditedAspect
   * @see AuditLogRepository
   * @see ConditionalOnMissingBean
   */
  @Bean
  @ConditionalOnMissingBean
  public AuditedAspect auditedAspect(AuditLogRepository auditLogRepository,
                                     AuditProperties auditProperties) {
    return new AuditedAspect(auditLogRepository, auditProperties);
  }
}