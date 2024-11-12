package com.mkhabibullin.audit.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "audit")
public class AuditProperties {
  private boolean enabled = true;
  private String defaultUsername = "anonymous";
  
}