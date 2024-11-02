package com.mkhabibullin.aspect;

import com.mkhabibullin.common.Audited;
import com.mkhabibullin.domain.model.AuditLog;
import com.mkhabibullin.domain.model.User;
import com.mkhabibullin.infrastructure.persistence.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * Test-specific aspect for auditing method executions in test environment.
 * This aspect is only active when the "test" profile is active.
 */
@Aspect
@Component
@Order(1)
@Profile("test")
public class TestAuditedAspect {
  private static final Logger log = LoggerFactory.getLogger(TestAuditedAspect.class);
  
  private final AuditLogRepository auditLogRepository;
  
  public TestAuditedAspect(AuditLogRepository auditLogRepository) {
    this.auditLogRepository = auditLogRepository;
  }
  
  @Pointcut("@annotation(audited)")
  public void auditedMethod(Audited audited) {
  }
  
  @Around(value = "@annotation(audited)", argNames = "joinPoint,audited")
  public Object writeTestAuditLog(ProceedingJoinPoint joinPoint, Audited audited) throws Throwable {
    if (!isTestMethod(joinPoint)) {
      return joinPoint.proceed();
    }
    log.debug("Test audit aspect is being executed for method: {}",
      joinPoint.getSignature().getName());
    long startTime = System.currentTimeMillis();
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String methodName = signature.getName();
    HttpServletRequest request = getCurrentRequest();
    String username = extractUsername(request);
    Object result = null;
    Throwable caughtThrowable = null;
    try {
      result = joinPoint.proceed();
      return result;
    } catch (Throwable throwable) {
      caughtThrowable = throwable;
      throw throwable;
    } finally {
      try {
        saveTestAuditLog(
          username,
          methodName,
          audited.audited(),
          System.currentTimeMillis() - startTime,
          request,
          caughtThrowable
        );
      } catch (Exception e) {
        log.error("Error saving test audit log: ", e);
      }
    }
  }
  
  private void saveTestAuditLog(
    String username,
    String methodName,
    String operation,
    long executionTime,
    HttpServletRequest request,
    Throwable throwable
  ) {
    try {
      if (throwable != null) {
        operation += " (Failed: " + throwable.getMessage() + ")";
      }
      AuditLog auditLog = new AuditLog(
        username,
        methodName,
        operation,
        LocalDateTime.now(),
        executionTime,
        request != null ? request.getRequestURI() : "test-uri",
        request != null ? request.getMethod() : "TEST"
      );
      auditLogRepository.save(auditLog);
      log.debug("Test audit log saved successfully for method: {}", methodName);
    } catch (Exception e) {
      log.error("Error saving test audit log: ", e);
    }
  }
  
  private HttpServletRequest getCurrentRequest() {
    ServletRequestAttributes attributes =
      (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    return attributes != null ? attributes.getRequest() : null;
  }
  
  private String extractUsername(HttpServletRequest request) {
    try {
      if (request != null && request.getSession(false) != null) {
        User user = (User) request.getSession(false).getAttribute("user");
        if (user != null) {
          return user.getName();
        }
      }
    } catch (Exception e) {
      log.warn("Error extracting username in test: ", e);
    }
    return "test-user";
  }
  
  private boolean isTestMethod(ProceedingJoinPoint joinPoint) {
    Class<?> targetClass = joinPoint.getTarget().getClass();
    if (isTestClass(targetClass)) {
      return true;
    }
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    return method.isAnnotationPresent(org.junit.jupiter.api.Test.class);
  }
  
  private boolean isTestClass(Class<?> clazz) {
    return clazz.getSimpleName().endsWith("Test") ||
           clazz.getSimpleName().endsWith("Tests") ||
           clazz.isAnnotationPresent(org.junit.jupiter.api.Test.class);
  }
}