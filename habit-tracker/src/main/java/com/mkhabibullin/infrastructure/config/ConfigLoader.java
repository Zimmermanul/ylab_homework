package com.mkhabibullin.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class ConfigLoader {
  private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
  private static final String CONFIG_FILE = "config.yml";
  private static final Map<String, Object> config;
  
  static {
    config = loadConfig();
  }
  
  private static Map<String, Object> loadConfig() {
    try (InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
      if (inputStream == null) {
        throw new RuntimeException("Configuration file " + CONFIG_FILE + " not found in classpath");
      }
      logger.info("Loading configuration from {}", CONFIG_FILE);
      Yaml yaml = new Yaml();
      Map<String, Object> loadedConfig = yaml.load(inputStream);
      logger.info("Configuration loaded successfully");
      return loadedConfig;
    } catch (Exception e) {
      logger.error("Failed to load configuration", e);
      throw new RuntimeException("Failed to load configuration", e);
    }
  }
  
  public static String getDatabaseUrl() {
    Map<String, Object> dbConfig = getConfigSection("database");
    return String.format("jdbc:postgresql://%s:%s/%s",
      dbConfig.get("host"),
      dbConfig.get("port"),
      dbConfig.get("name"));
  }
  
  public static String getDatabaseUser() {
    return getConfigSection("database").get("user").toString();
  }
  
  public static String getDatabasePassword() {
    return getConfigSection("database").get("password").toString();
  }
  
  public static boolean isLiquibaseEnabled() {
    return (boolean) getConfigSection("liquibase").get("enabled");
  }
  
  public static String getLiquibaseChangeLogFile() {
    return getConfigSection("liquibase").get("changeLogFile").toString();
  }
  
  @SuppressWarnings("unchecked")
  private static Map<String, Object> getConfigSection(String section) {
    Map<String, Object> sectionConfig = (Map<String, Object>) config.get(section);
    if (sectionConfig == null) {
      throw new RuntimeException("Configuration section '" + section + "' not found");
    }
    return sectionConfig;
  }
}