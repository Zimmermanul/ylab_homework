package com.mkhabibullin.aspect;

import com.mkhabibullin.common.Audited;
import com.mkhabibullin.domain.model.AuditLog;
import com.mkhabibullin.domain.model.User;
import com.mkhabibullin.infrastructure.persistence.repository.AuditLogDbRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
public class TestAuditedServletAspect {
  private static volatile DataSource testDataSource;
  private static final TestAuditedServletAspect INSTANCE = new TestAuditedServletAspect();
  
  public static TestAuditedServletAspect aspectOf() {
    return INSTANCE;
  }
  
  public static synchronized void setTestDataSource(DataSource dataSource) {
    testDataSource = dataSource;
  }
  
  private TestAuditedServletAspect() {
  }
  
  @Pointcut("@annotation(audited)")
  public void auditedMethod(Audited audited) {
  }
  
  @Around(value = "auditedMethod(audited) && args(.., req, resp)",
    argNames = "joinPoint,audited,req,resp")
  public Object writeAuditLog(ProceedingJoinPoint joinPoint, Audited audited,
                              HttpServletRequest req, HttpServletResponse resp) throws Throwable {
    if (!isCalledFromTest() || testDataSource == null) {
      return joinPoint.proceed();
    }
    System.out.println("Test Audit aspect is being executed!");
    long startTime = System.currentTimeMillis();
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String methodName = signature.getName();
    String username = extractUsername(req);
    AuditLogDbRepository auditLogRepository = new AuditLogDbRepository(testDataSource);
    Object result;
    Throwable caughtThrowable = null;
    try {
      result = joinPoint.proceed();
      return result;
    } catch (Throwable throwable) {
      caughtThrowable = throwable;
      throw throwable;
    } finally {
      long executionTime = System.currentTimeMillis() - startTime;
      saveAuditLog(auditLogRepository, username, methodName,
        audited.audited(), executionTime, req, caughtThrowable);
    }
  }
  
  private void saveAuditLog(AuditLogDbRepository repository, String username,
                            String methodName, String operation, long executionTime,
                            HttpServletRequest request, Throwable throwable) {
    try {
      AuditLog auditLog = new AuditLog(
        username,
        methodName,
        operation,
        LocalDateTime.now(),
        executionTime,
        request != null ? request.getRequestURI() : null,
        request != null ? request.getMethod() : null
      );
      repository.save(auditLog);
    } catch (Exception e) {
      System.err.println("Error saving audit log: \n " + e.getMessage());
    }
  }
  
  private String extractUsername(HttpServletRequest request) {
    try {
      if (request != null) {
        HttpSession session = request.getSession(false);
        if (session != null) {
          User user = (User) session.getAttribute("user");
          if (user != null) {
            return user.getName();
          }
        }
      }
    } catch (Exception e) {
      System.err.println("Error extracting username: " + e.getMessage());
    }
    return "anonymous";
  }
  
  private boolean isCalledFromTest() {
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    for (StackTraceElement element : stackTrace) {
      if (element.getClassName().endsWith("Test")) {
        try {
          Class<?> callingClass = Class.forName(element.getClassName());
          if (hasTestAnnotations(callingClass)) {
            return true;
          }
        } catch (ClassNotFoundException e) {
        }
      }
    }
    return false;
  }
  
  private boolean hasTestAnnotations(Class<?> clazz) {
    if (clazz.isAnnotationPresent(org.junit.jupiter.api.Test.class)) {
      return true;
    }
    for (Method method : clazz.getDeclaredMethods()) {
      if (method.isAnnotationPresent(org.junit.jupiter.api.Test.class)) {
        return true;
      }
    }
    return false;
  }
}