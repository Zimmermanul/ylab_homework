package com.mkhabibullin.audit.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the audit functionality.
 * These properties can be configured in application.properties/yml using the 'audit' prefix.
 *
 * <p>Example configuration in application.yml:</p>
 * <pre>
 * audit:
 *   enabled: true
 *   defaultUsername: system
 * </pre>
 *
 * <p>All properties have default values and are optional.</p>
 *
 * @see ConfigurationProperties
 * @see com.mkhabibullin.audit.aspect.AuditedAspect
 */
@ConfigurationProperties(prefix = "audit")
@Getter
public class AuditProperties {
  private boolean enabled = true;
  private String defaultUsername = "anonymous";
  
}