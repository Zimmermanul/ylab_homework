package com.mkhabibullin.app.aspect;

import com.mkhabibullin.app.annotation.Audited;
import com.mkhabibullin.app.data.AuditLogDbRepository;
import com.mkhabibullin.app.model.AuditLog;
import com.mkhabibullin.app.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.time.LocalDateTime;

/**
 * Aspect for handling audit logging of method executions.
 * This aspect intercepts methods annotated with @Audited and logs their execution details.
 * It captures timing information, user details, and request context when available.
 */
@Aspect
public class AuditAspect {
  private final AuditLogDbRepository auditLogRepository;
  
  /**
   * Constructs a new AuditAspect with the specified repository.
   *
   * @param auditLogRepository The repository to be used for storing audit logs
   */
  public AuditAspect(AuditLogDbRepository auditLogRepository) {
    this.auditLogRepository = auditLogRepository;
  }
  
  /**
   * Pointcut that matches all methods annotated with @Audited.
   * This pointcut is used to define which methods should be audited.
   */
  @Pointcut("@annotation(com.mkhabibullin.app.annotation.Audited)")
  public void auditedMethod() {
  }
  
  /**
   * Pointcut that matches all methods with HttpServletRequest parameter.
   * This is used to identify web-specific methods.
   */
  @Pointcut("execution(* *(.., javax.servlet.http.HttpServletRequest, ..))")
  public void webMethod() {
  }
  
  /**
   * Around advice that intercepts method calls matching the auditedMethod pointcut.
   * This method measures execution time and logs the operation details.
   *
   * @param joinPoint The join point representing the intercepted method call
   * @return The result of the method execution
   * @throws Throwable if the method execution fails
   */
  @Around("auditedMethod() && @annotation(audited)")
  public Object logAuditEvent(ProceedingJoinPoint joinPoint, Audited audited) throws Throwable {
    long startTime = System.currentTimeMillis();
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String methodName = signature.getName();
    HttpServletRequest request = extractHttpRequest(joinPoint);
    String username = extractUsername(request);
    try {
      Object result = joinPoint.proceed();
      long executionTime = System.currentTimeMillis() - startTime;
      AuditLog auditLog = createAuditLog(
        username,
        methodName,
        audited.operation(),
        executionTime,
        request
      );
      auditLogRepository.save(auditLog);
      return result;
    } catch (Throwable throwable) {
      long executionTime = System.currentTimeMillis() - startTime;
      AuditLog auditLog = createAuditLog(
        username,
        methodName,
        audited.operation() + " (Failed: " + throwable.getMessage() + ")",
        executionTime,
        request
      );
      auditLogRepository.save(auditLog);
      throw throwable;
    }
  }
  
  /**
   * Extracts HttpServletRequest from method arguments.
   *
   * @param joinPoint The join point containing method arguments
   * @return The HttpServletRequest if found, null otherwise
   */
  private HttpServletRequest extractHttpRequest(ProceedingJoinPoint joinPoint) {
    for (Object arg : joinPoint.getArgs()) {
      if (arg instanceof HttpServletRequest) {
        return (HttpServletRequest) arg;
      }
    }
    return null;
  }
  
  /**
   * Extracts username from the request session.
   * Returns "anonymous" if no user is found in the session.
   *
   * @param request The HTTP request object
   * @return The username from the session or "anonymous"
   */
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
  
  /**
   * Creates an AuditLog object with the provided information.
   *
   * @param username      The username performing the operation
   * @param methodName    The name of the method being executed
   * @param operation     The operation description
   * @param executionTime The execution time in milliseconds
   * @param request       The HTTP request object
   * @return A new AuditLog object
   */
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
}