package com.mkhabibullin.aspect;

import com.mkhabibullin.common.Audited;
import com.mkhabibullin.domain.model.AuditLog;
import com.mkhabibullin.domain.model.User;
import com.mkhabibullin.infrastructure.persistence.repository.AuditLogDbRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import javax.sql.DataSource;
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
  
  @Around("auditedMethod(audited)")
  public Object auditMethod(ProceedingJoinPoint joinPoint, Audited audited) throws Throwable {
    if (testDataSource == null) {
      return joinPoint.proceed();
    }
    System.out.println("Test Audit aspect is being executed!");
    long startTime = System.currentTimeMillis();
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    String methodName = signature.getName();
    HttpServletRequest request = null;
    for (Object arg : joinPoint.getArgs()) {
      if (arg instanceof HttpServletRequest) {
        request = (HttpServletRequest) arg;
        break;
      }
    }
    String username = extractUsername(request);
    AuditLogDbRepository auditLogRepository = new AuditLogDbRepository(testDataSource);
    try {
      Object result = joinPoint.proceed();
      long executionTime = System.currentTimeMillis() - startTime;
      saveAuditLog(auditLogRepository, username, methodName, audited.audited(),
        executionTime, request, null);
      return result;
    } catch (Throwable throwable) {
      long executionTime = System.currentTimeMillis() - startTime;
      saveAuditLog(auditLogRepository, username, methodName,
        audited.audited(), executionTime, request, throwable);
      throw throwable;
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
}