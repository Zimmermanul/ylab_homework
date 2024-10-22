package com.mkhabibullin.app.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

import static com.mkhabibullin.app.util.DbUtil.createHikariConfig;

/**
 * The {@code DataSourceConfig} class provides utility methods for configuring and
 * obtaining a {@link javax.sql.DataSource} using the HikariCP connection pool.
 */
public class DataSourceConfig {
  
  private DataSourceConfig() {
  }
  public static DataSource getDataSource() {
    HikariConfig config = createHikariConfig();
    return new HikariDataSource(config);
  }
}