package com.mkhabibullin.aspect;

import javax.sql.DataSource;
import java.util.concurrent.Callable;

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
  
  public static DataSource getTestDataSource() {
    return testDataSource.get();
  }
  
  public static Runnable wrapWithContext(Runnable runnable) {
    Boolean currentIsTest = isTestContext.get();
    DataSource currentDataSource = testDataSource.get();
    return () -> {
      Boolean previousIsTest = isTestContext.get();
      DataSource previousDataSource = testDataSource.get();
      try {
        if (currentIsTest != null) {
          isTestContext.set(currentIsTest);
        }
        if (currentDataSource != null) {
          testDataSource.set(currentDataSource);
        }
        runnable.run();
      } finally {
        if (previousIsTest != null) {
          isTestContext.set(previousIsTest);
        } else {
          isTestContext.remove();
        }
        if (previousDataSource != null) {
          testDataSource.set(previousDataSource);
        } else {
          testDataSource.remove();
        }
      }
    };
  }
  
  public static <V> Callable<V> wrapWithContext(Callable<V> callable) {
    Boolean currentIsTest = isTestContext.get();
    DataSource currentDataSource = testDataSource.get();
    return () -> {
      Boolean previousIsTest = isTestContext.get();
      DataSource previousDataSource = testDataSource.get();
      try {
        if (currentIsTest != null) {
          isTestContext.set(currentIsTest);
        }
        if (currentDataSource != null) {
          testDataSource.set(currentDataSource);
        }
        return callable.call();
      } finally {
        if (previousIsTest != null) {
          isTestContext.set(previousIsTest);
        } else {
          isTestContext.remove();
        }
        if (previousDataSource != null) {
          testDataSource.set(previousDataSource);
        } else {
          testDataSource.remove();
        }
      }
    };
  }
}
