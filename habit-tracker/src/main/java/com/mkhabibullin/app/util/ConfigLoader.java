package com.mkhabibullin.app.util;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;

public class ConfigLoader {
  private static final String CONFIG_FILE = "config.yml";
  private static Map<String, Object> config;
  
  static {
    loadConfig();
  }
  private static void loadConfig() {
    try (InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
      Yaml yaml = new Yaml();
      config = yaml.load(inputStream);
    } catch (Exception e) {
      throw new RuntimeException("Failed to load configuration", e);
    }
  }
  
  public static String getDatabaseUrl() {
    Map<String, Object> dbConfig = (Map<String, Object>) config.get("database");
    return String.format("jdbc:postgresql://%s:%s/%s",
      dbConfig.get("host"),
      dbConfig.get("port"),
      dbConfig.get("name"));
  }
  
  public static String getDatabaseUser() {
    return ((Map<String, Object>) config.get("database")).get("user").toString();
  }
  
  public static String getDatabasePassword() {
    return ((Map<String, Object>) config.get("database")).get("password").toString();
  }
  
  public static boolean isMigrationsEnabled() {
    return (boolean) ((Map<String, Object>) config.get("migrations")).get("enabled");
  }
  
  public static String getMigrationsLocation() {
    return ((Map<String, Object>) config.get("migrations")).get("location").toString();
  }
  
  public static boolean isLiquibaseEnabled() {
    return (boolean) ((Map<String, Object>) config.get("liquibase")).get("enabled");
  }
  
  public static String getLiquibaseChangeLogFile() {
    return ((Map<String, Object>) config.get("liquibase")).get("changeLogFile").toString();
  }
}