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
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Aspect for auditing method executions using Spring AOP.
 * Records method execution details including timing and user information.
 */
@Aspect
@Component
@Order(2)
@Profile("!test")
public class AuditedAspect {
  private static final Logger log = LoggerFactory.getLogger(AuditedAspect.class);
  
  private final AuditLogRepository auditLogRepository;
  private final Environment environment;
  
  public AuditedAspect(AuditLogRepository auditLogRepository, Environment environment) {
    this.auditLogRepository = auditLogRepository;
    this.environment = environment;
  }
  
  @Pointcut("@annotation(audited)")
  public void auditedMethod(Audited audited) {
  }
  
  @Around(value = "@annotation(audited)", argNames = "joinPoint,audited")
  public Object writeAuditLog(ProceedingJoinPoint joinPoint, Audited audited) throws Throwable {
    if (isTestEnvironment()) {
      return joinPoint.proceed();
    }
    log.debug("Starting audit logging for method: {}", joinPoint.getSignature().getName());
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
        log.debug("Audit log saved successfully for method: {}", methodName);
      } catch (Exception e) {
        log.error("Error saving audit log: ", e);
      }
    }
  }
  
  private HttpServletRequest getCurrentRequest() {
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
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
      log.warn("Error extracting username: ", e);
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
    String requestUri = request != null ? request.getRequestURI() : "N/A";
    String requestMethod = request != null ? request.getMethod() : "N/A";
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
  
  private boolean isTestEnvironment() {
    return Arrays.asList(environment.getActiveProfiles()).contains("test") ||
           Arrays.stream(environment.getActiveProfiles())
             .anyMatch(profile -> profile.contains("test"));
  }
}