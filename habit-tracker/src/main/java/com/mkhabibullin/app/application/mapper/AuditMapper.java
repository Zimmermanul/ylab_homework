package com.mkhabibullin.app.application.mapper;

import com.mkhabibullin.app.application.validation.AuditMapperValidator;
import com.mkhabibullin.app.domain.exception.ValidationException;
import com.mkhabibullin.app.domain.model.AuditLog;
import com.mkhabibullin.app.domain.model.AuditStatistics;
import com.mkhabibullin.app.presentation.dto.audit.AuditLogResponseDTO;
import com.mkhabibullin.app.presentation.dto.audit.AuditStatisticsDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper class for converting between Audit entities and DTOs.
 * This mapper handles audit data transformations while performing validation
 * through {@link AuditMapperValidator}.
 */
public class AuditMapper {
  private static final AuditMapper INSTANCE = new AuditMapper();
  private final AuditMapperValidator validator;
  
  private AuditMapper() {
    this.validator = new AuditMapperValidator();
  }
  
  public static AuditMapper getInstance() {
    return INSTANCE;
  }
  
  /**
   * Converts an audit log entity to a response DTO.
   *
   * @param auditLog The audit log entity to convert
   * @return The corresponding response DTO
   * @throws ValidationException if validation fails
   */
  public AuditLogResponseDTO auditLogToResponseDto(AuditLog auditLog) throws ValidationException {
    if (auditLog == null) {
      throw new ValidationException("Audit log cannot be null");
    }
    validator.validateAuditLog(auditLog);
    return new AuditLogResponseDTO(
      auditLog.getId(),
      auditLog.getUsername(),
      auditLog.getMethodName(),
      auditLog.getOperation(),
      auditLog.getTimestamp(),
      auditLog.getExecutionTimeMs(),
      auditLog.getRequestUri(),
      auditLog.getRequestMethod()
    );
  }
  
  /**
   * Converts audit statistics entity to a response DTO.
   *
   * @param statistics The audit statistics entity to convert
   * @return The corresponding statistics DTO
   * @throws ValidationException if validation fails
   */
  public AuditStatisticsDTO statisticsToDto(AuditStatistics statistics) throws ValidationException {
    if (statistics == null) {
      throw new ValidationException("Statistics cannot be null");
    }
    validator.validateAuditStatistics(statistics);
    return new AuditStatisticsDTO(
      statistics.getTotalOperations(),
      statistics.getAverageExecutionTime(),
      statistics.getOperationCounts(),
      statistics.getUserActivityCounts(),
      statistics.getAverageTimeByOperation(),
      statistics.getMostActiveUser(),
      statistics.getMostCommonOperation(),
      statistics.getPeriodStart(),
      statistics.getPeriodEnd()
    );
  }
  
  /**
   * Converts a list of audit logs to response DTOs.
   *
   * @param logs The list of audit log entities
   * @return List of corresponding response DTOs
   * @throws ValidationException if validation fails for any log entry
   */
  public List<AuditLogResponseDTO> auditLogsToResponseDtos(List<AuditLog> logs) throws ValidationException {
    if (logs == null) {
      return new ArrayList<>();
    }
    List<String> errors = new ArrayList<>();
    List<AuditLogResponseDTO> dtos = new ArrayList<>();
    
    for (AuditLog log : logs) {
      try {
        dtos.add(auditLogToResponseDto(log));
      } catch (ValidationException e) {
        errors.addAll(e.getValidationErrors());
      }
    }
    if (!errors.isEmpty()) {
      throw new ValidationException(errors);
    }
    return dtos;
  }
}