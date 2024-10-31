package com.mkhabibullin.aspect;

import javax.sql.DataSource;

public class AspectContext {
  private static final ThreadLocal<Boolean> isTestContext = new ThreadLocal<>();
  private static final ThreadLocal<DataSource> testDataSource = new ThreadLocal<>();
  
  public static void setTestContext(DataSource dataSource) {
    isTestContext.set(true);
    testDataSource.set(dataSource);
  }
  
  public static void clearTestContext() {
    isTestContext.remove();
    testDataSource.remove();
  }
  
  public static boolean isTestContext() {
    return Boolean.TRUE.equals(isTestContext.get());
  }
  
  public static DataSource getTestDataSource() {
    return testDataSource.get();
  }
}
