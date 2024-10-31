package com.mkhabibullin.aspect;

import com.mkhabibullin.common.Audited;
import com.mkhabibullin.domain.model.AuditLog;
import com.mkhabibullin.domain.model.User;
import com.mkhabibullin.infrastructure.config.DataSourceConfig;
import com.mkhabibullin.infrastructure.persistence.repository.AuditLogDbRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.time.LocalDateTime;

@Aspect
public class AuditedServletAspect {
  private static final AuditedServletAspect INSTANCE = new AuditedServletAspect();
  
  public static AuditedServletAspect aspectOf() {
    return INSTANCE;
  }
  
  private AuditedServletAspect() {
  }
  
  @Pointcut("@annotation(audited)")
  public void auditedMethod(Audited audited) {
  }
  
  @Around(value = "auditedMethod(audited) && args(.., req, resp)",
    argNames = "joinPoint,audited,req,resp")
  public Object writeAuditLog(ProceedingJoinPoint joinPoint, Audited audited, HttpServletRequest req, HttpServletResponse resp) throws Throwable {
    if (isCalledFromTest()) {
      return joinPoint.proceed();
    }
    System.out.println("Audit aspect is being executed!");
    long startTime = System.currentTimeMillis();
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String methodName = signature.getName();
    HttpServletRequest request = extractHttpRequest(joinPoint);
    String username = extractUsername(request);
    AuditLogDbRepository auditLogRepository = new AuditLogDbRepository(DataSourceConfig.getDataSource());
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
      String operation = audited.audited();
      if (caughtThrowable != null) {
        operation += " (Failed: " + caughtThrowable.getMessage() + ")";
      }
      AuditLog auditLog = createAuditLog(
        username,
        methodName,
        operation,
        executionTime,
        request
      );
      auditLogRepository.save(auditLog);
    }
  }
  
  private HttpServletRequest extractHttpRequest(ProceedingJoinPoint joinPoint) {
    for (Object arg : joinPoint.getArgs()) {
      if (arg instanceof HttpServletRequest) {
        return (HttpServletRequest) arg;
      }
    }
    return null;
  }
  
  private String extractUsername(HttpServletRequest request) {
    if (request != null) {
      HttpSession session = request.getSession(false);
      if (session != null) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
          return user.getName();
        }
      }
    }
    return "anonymous";
  }
  
  private AuditLog createAuditLog(
    String username,
    String methodName,
    String operation,
    long executionTime,
    HttpServletRequest request
  ) {
    String requestUri = request != null ? request.getRequestURI() : null;
    String requestMethod = request != null ? request.getMethod() : null;
    return new AuditLog(
      username,
      methodName,
      operation,
      LocalDateTime.now(),
      executionTime,
      requestUri,
      requestMethod
    );
  }
  
  private boolean isCalledFromTest() {
    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
    for (StackTraceElement element : stackTrace) {
      String className = element.getClassName();
      if (className.endsWith("Test") ||
          className.contains("Test$") ||
          className.startsWith("org.junit.") ||
          element.getMethodName().startsWith("test")) {
        return true;
      }
    }
    return false;
  }
}