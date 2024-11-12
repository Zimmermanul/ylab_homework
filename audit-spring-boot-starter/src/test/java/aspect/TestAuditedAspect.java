package aspect;

import com.mkhabibullin.audit.annotation.Audited;
import com.mkhabibullin.audit.domain.model.AuditLog;
import com.mkhabibullin.audit.persistence.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
@Slf4j
public class TestAuditedAspect {
  private final AuditLogRepository auditLogRepository;
  
  /**
   * Constructs a new TestAuditedAspect with the required repository.
   *
   * @param auditLogRepository Repository for persisting audit log entries
   */
  public TestAuditedAspect(AuditLogRepository auditLogRepository) {
    this.auditLogRepository = auditLogRepository;
  }
  
  /**
   * Defines pointcut for methods annotated with @Audited.
   */
  @Pointcut("@annotation(audited)")
  public void auditedMethod(Audited audited) {
  }
  
  /**
   * Around advice that handles the audit logging for test method executions.
   */
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
      AuditLog auditLog = new AuditLog();
      auditLog.setUsername(username);
      auditLog.setMethodName(methodName);
      auditLog.setOperation(operation);
      auditLog.setTimestamp(LocalDateTime.now());
      auditLog.setExecutionTimeMs(executionTime);
      auditLog.setRequestUri(request != null ? request.getRequestURI() : "test-uri");
      auditLog.setRequestMethod(request != null ? request.getMethod() : "TEST");
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
  
  private String extractUsername() {
    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication != null && authentication.isAuthenticated()) {
        return authentication.getName();
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
    return method.isAnnotationPresent(Test.class);
  }
  
  private boolean isTestClass(Class<?> clazz) {
    return clazz.getSimpleName().endsWith("Test") ||
           clazz.getSimpleName().endsWith("Tests") ||
           clazz.isAnnotationPresent(Test.class);
  }
}