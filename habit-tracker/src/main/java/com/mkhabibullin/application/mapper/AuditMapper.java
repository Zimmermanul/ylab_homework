package com.mkhabibullin.application.mapper;

import com.mkhabibullin.application.validation.AuditMapperValidator;
import com.mkhabibullin.domain.model.AuditLog;
import com.mkhabibullin.domain.model.AuditStatistics;
import com.mkhabibullin.presentation.dto.audit.AuditLogResponseDTO;
import com.mkhabibullin.presentation.dto.audit.AuditStatisticsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper interface for converting between Audit DTOs and entities.
 * This mapper handles audit data transformations while performing validation
 * through {@link AuditMapperValidator}.
 *
 * <p>The mapper is configured with:</p>
 * <ul>
 *   <li>Default component model for simple instantiation</li>
 *   <li>Integration with {@link AuditMapperValidator} for field validation</li>
 *   <li>A singleton INSTANCE for stateless mapping operations</li>
 * </ul>
 *
 * @see org.mapstruct.Mapper
 * @see AuditMapperValidator
 * @see AuditLog
 * @see AuditStatistics
 */
@Mapper(componentModel = "default", uses = AuditMapperValidator.class)
public interface AuditMapper {
  
  AuditMapper INSTANCE = Mappers.getMapper(AuditMapper.class);
  
  @Mapping(target = "id", source = "id", qualifiedByName = "validateId")
  @Mapping(target = "username", source = "username", qualifiedByName = "validateUsername")
  @Mapping(target = "methodName", source = "methodName", qualifiedByName = "validateMethodName")
  @Mapping(target = "operation", source = "operation", qualifiedByName = "validateOperation")
  @Mapping(target = "timestamp", source = "timestamp", qualifiedByName = "validateTimestamp")
  @Mapping(target = "executionTimeMs", source = "executionTimeMs", qualifiedByName = "validateExecutionTime")
  @Mapping(target = "requestUri", source = "requestUri", qualifiedByName = "validateRequestUri")
  @Mapping(target = "requestMethod", source = "requestMethod", qualifiedByName = "validateRequestMethod")
  AuditLogResponseDTO auditLogToResponseDto(AuditLog auditLog);
  
  List<AuditLogResponseDTO> auditLogsToResponseDtos(List<AuditLog> logs);
  
  @Mapping(target = "totalOperations", source = "totalOperations", qualifiedByName = "validateTotalOperations")
  @Mapping(target = "averageExecutionTime", source = "averageExecutionTime", qualifiedByName = "validateAverageExecutionTime")
  @Mapping(target = "operationCounts", source = "operationCounts", qualifiedByName = "validateOperationCounts")
  @Mapping(target = "userActivityCounts", source = "userActivityCounts", qualifiedByName = "validateUserActivityCounts")
  @Mapping(target = "averageTimeByOperation", source = "averageTimeByOperation", qualifiedByName = "validateAverageTimeByOperation")
  @Mapping(target = "mostActiveUser", source = "mostActiveUser", qualifiedByName = "validateMostActiveUser")
  @Mapping(target = "mostCommonOperation", source = "mostCommonOperation", qualifiedByName = "validateMostCommonOperation")
  @Mapping(target = "periodStart", source = "periodStart", qualifiedByName = "validatePeriodStart")
  @Mapping(target = "periodEnd", source = "periodEnd", qualifiedByName = "validatePeriodEnd")
  AuditStatisticsDTO statisticsToDto(AuditStatistics statistics);
}