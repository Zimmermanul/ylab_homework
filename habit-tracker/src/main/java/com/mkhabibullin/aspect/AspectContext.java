package com.mkhabibullin.aspect;

import javax.sql.DataSource;

public class AspectContext {
  private static final InheritableThreadLocal<Boolean> isTestContext = new InheritableThreadLocal<>();
  private static final InheritableThreadLocal<DataSource> testDataSource = new InheritableThreadLocal<>();
  
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
}
