package com.mkhabibullin.audit.aspect;

import com.mkhabibullin.audit.annotation.Audited;
import com.mkhabibullin.audit.domain.model.AuditLog;
import com.mkhabibullin.audit.persistence.repository.AuditLogRepository;
import com.mkhabibullin.audit.properties.AuditProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

/**
 * Aspect for auditing method executions using Spring AOP.
 * Records method execution details including timing and user information.
 */
@Aspect
@Order(2)
@Profile("!test")
@ConditionalOnClass(AuditLogRepository.class)
@RequiredArgsConstructor
@Slf4j
public class AuditedAspect {
  private final AuditLogRepository auditLogRepository;
  private final AuditProperties auditProperties;
  
  /**
   * Pointcut definition for methods annotated with @Audited.
   *
   * @param audited the Audited annotation instance
   */
  @Pointcut("@annotation(audited)")
  public void auditedMethod(Audited audited) {
  }
  
  /**
   * Around advice that handles the audit logging process for annotated methods.
   * Records method execution time, user information, and operation details.
   * Saves the audit log entry after method execution, including any failure information.
   *
   * @param joinPoint the join point representing the intercepted method
   * @param audited   the Audited annotation instance containing audit configuration
   * @return the result of the method execution
   * @throws Throwable if the underlying method throws an exception
   */
  @Around(value = "@annotation(audited)", argNames = "joinPoint,audited")
  public Object writeAuditLog(ProceedingJoinPoint joinPoint, Audited audited) throws Throwable {
    log.debug("Starting audit logging for method: {}", joinPoint.getSignature().getName());
    long startTime = System.currentTimeMillis();
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String methodName = signature.getName();
    HttpServletRequest request = getCurrentRequest();
    String username = extractUsername();
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
  
  private String extractUsername() {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null && authentication.isAuthenticated()) {
        return authentication.getName();
      }
    } catch (Exception e) {
      log.warn("Error extracting username: ", e);
    }
    return auditProperties.getDefaultUsername();
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
}