package com.mkhabibullin.audit.application.mapper;

import com.mkhabibullin.audit.domain.model.AuditLog;
import com.mkhabibullin.audit.domain.model.AuditStatistics;
import com.mkhabibullin.audit.presentation.dto.AuditLogResponseDTO;
import com.mkhabibullin.audit.presentation.dto.AuditStatisticsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper interface for converting between Audit entities and DTOs.
 * Provides mapping functionality for audit-related data transformations.
 */
@Mapper(
  componentModel = "spring",
  unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AuditMapper {
  
  /**
   * Converts an audit log entity to a response DTO.
   *
   * @param auditLog the audit log entity
   * @return the mapped audit log response DTO
   */
  @Mapping(target = "id", source = "id")
  @Mapping(target = "username", source = "username")
  @Mapping(target = "methodName", source = "methodName")
  @Mapping(target = "operation", source = "operation")
  @Mapping(target = "timestamp", source = "timestamp")
  @Mapping(target = "executionTimeMs", source = "executionTimeMs")
  @Mapping(target = "requestUri", source = "requestUri")
  @Mapping(target = "requestMethod", source = "requestMethod")
  AuditLogResponseDTO auditLogToResponseDto(AuditLog auditLog);
  
  /**
   * Converts a list of audit log entities to response DTOs.
   *
   * @param logs list of audit log entities
   * @return list of audit log response DTOs
   */
  List<AuditLogResponseDTO> auditLogsToResponseDtos(List<AuditLog> logs);
  
  /**
   * Converts audit statistics to DTO.
   *
   * @param statistics the audit statistics entity
   * @return the mapped audit statistics DTO
   */
  @Mapping(target = "totalOperations", source = "totalOperations")
  @Mapping(target = "averageExecutionTime", source = "averageExecutionTime")
  @Mapping(target = "operationCounts", source = "operationCounts")
  @Mapping(target = "userActivityCounts", source = "userActivityCounts")
  @Mapping(target = "averageTimeByOperation", source = "averageTimeByOperation")
  @Mapping(target = "mostActiveUser", source = "mostActiveUser")
  @Mapping(target = "mostCommonOperation", source = "mostCommonOperation")
  @Mapping(target = "periodStart", source = "periodStart")
  @Mapping(target = "periodEnd", source = "periodEnd")
  AuditStatisticsDTO statisticsToDto(AuditStatistics statistics);
}