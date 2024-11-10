package tests;

import com.mkhabibullin.application.mapper.AuditMapper;
import com.mkhabibullin.application.service.AuditLogService;
import com.mkhabibullin.application.validation.AuditValidator;
import com.mkhabibullin.domain.model.AuditLog;
import com.mkhabibullin.domain.model.AuditStatistics;
import com.mkhabibullin.presentation.controller.AuditRestController;
import com.mkhabibullin.presentation.dto.audit.AuditLogResponseDTO;
import com.mkhabibullin.presentation.dto.audit.AuditStatisticsDTO;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuditRestControllerTest extends BaseTest {
  @Mock
  private AuditLogService auditLogService;
  @Mock
  private AuditMapper auditMapper;
  @Mock
  private AuditValidator auditValidator;
  private AuditRestController auditController;
  
  @Override
  protected void setupMockMvc() {
    auditController = new AuditRestController(auditLogService, auditMapper, auditValidator);
    mockMvc = buildMockMvc(auditController);
  }
  
  @Test
  void getRecentLogsShouldReturnLogs() throws Exception {
    int limit = 10;
    List<AuditLog> auditLogs = Arrays.asList(
      createTestAuditLog("User Login"),
      createTestAuditLog("Create Habit")
    );
    List<AuditLogResponseDTO> responseDTOs = Arrays.asList(
      createTestAuditLogDTO(1L, "User Login"),
      createTestAuditLogDTO(2L, "Create Habit")
    );
    given(auditLogService.getRecentLogs(limit)).willReturn(auditLogs);
    given(auditMapper.auditLogsToResponseDtos(auditLogs)).willReturn(responseDTOs);
    performRequest(get("/api/audit-logs/recent")
      .param("limit", String.valueOf(limit)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].operation").value("User Login"))
      .andExpect(jsonPath("$[1].operation").value("Create Habit"));
    verify(auditLogService).getRecentLogs(limit);
    verify(auditMapper).auditLogsToResponseDtos(auditLogs);
  }
  
  @Test
  void getRecentLogsWithInvalidLimitShouldReturnBadRequest() throws Exception {
    performRequest(get("/api/audit-logs/recent")
      .param("limit", "0"))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("Limit must be between 1 and 100"));
  }
  
  @Test
  void getUserLogsShouldReturnUserSpecificLogs() throws Exception {
    String username = "testuser";
    List<AuditLog> auditLogs = Arrays.asList(
      createTestAuditLog("View Habits"),
      createTestAuditLog("Update Profile")
    );
    List<AuditLogResponseDTO> responseDTOs = Arrays.asList(
      createTestAuditLogDTO(1L, "View Habits"),
      createTestAuditLogDTO(2L, "Update Profile")
    );
    given(auditLogService.getUserLogs(username)).willReturn(auditLogs);
    given(auditMapper.auditLogsToResponseDtos(auditLogs)).willReturn(responseDTOs);
    performRequest(get("/api/audit-logs/user/" + username))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].username").value(username));
    verify(auditLogService).getUserLogs(username);
    verify(auditMapper).auditLogsToResponseDtos(auditLogs);
  }
  
  @Test
  void getOperationLogsShouldReturnOperationSpecificLogs() throws Exception {
    String operation = "Create Habit";
    List<AuditLog> auditLogs = Arrays.asList(
      createTestAuditLog(operation),
      createTestAuditLog(operation)
    );
    List<AuditLogResponseDTO> responseDTOs = Arrays.asList(
      createTestAuditLogDTO(1L, operation),
      createTestAuditLogDTO(2L, operation)
    );
    given(auditLogService.getOperationLogs(operation)).willReturn(auditLogs);
    given(auditMapper.auditLogsToResponseDtos(auditLogs)).willReturn(responseDTOs);
    performRequest(get("/api/audit-logs/operation/" + operation))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].operation").value(operation));
    verify(auditLogService).getOperationLogs(operation);
    verify(auditMapper).auditLogsToResponseDtos(auditLogs);
  }
  
  @Test
  void getStatisticsShouldReturnAuditStatistics() throws Exception {
    LocalDateTime startDateTime = LocalDateTime.now().minusDays(7);
    LocalDateTime endDateTime = LocalDateTime.now();
    Map<String, Long> operationCounts = new HashMap<>();
    operationCounts.put("Create Habit", 5L);
    operationCounts.put("Update Habit", 3L);
    Map<String, Long> userActivityCounts = new HashMap<>();
    userActivityCounts.put("user1", 4L);
    userActivityCounts.put("user2", 4L);
    Map<String, Double> averageTimeByOperation = new HashMap<>();
    averageTimeByOperation.put("Create Habit", 150.0);
    averageTimeByOperation.put("Update Habit", 120.0);
    AuditStatistics statistics = new AuditStatistics(
      8L,
      135.0,
      operationCounts,
      userActivityCounts,
      averageTimeByOperation,
      "user1",
      "Create Habit",
      startDateTime,
      endDateTime
    );
    AuditStatisticsDTO statisticsDTO = new AuditStatisticsDTO(
      8L,
      135.0,
      operationCounts,
      userActivityCounts,
      averageTimeByOperation,
      "user1",
      "Create Habit",
      startDateTime,
      endDateTime
    );
    given(auditLogService.getStatistics(startDateTime, endDateTime))
      .willReturn(statistics);
    given(auditMapper.statisticsToDto(statistics))
      .willReturn(statisticsDTO);
    performRequest(get("/api/audit-logs/statistics")
      .param("startDateTime", startDateTime.toString())
      .param("endDateTime", endDateTime.toString()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.totalOperations").value(8))
      .andExpect(jsonPath("$.averageExecutionTime").value(135.0))
      .andExpect(jsonPath("$.mostActiveUser").value("user1"))
      .andExpect(jsonPath("$.mostCommonOperation").value("Create Habit"))
      .andExpect(jsonPath("$.operationCounts['Create Habit']").value(5))
      .andExpect(jsonPath("$.userActivityCounts['user1']").value(4));
    verify(auditLogService).getStatistics(startDateTime, endDateTime);
    verify(auditMapper).statisticsToDto(statistics);
  }
  
  @Test
  void getStatisticsWithInvalidDateRangeShouldReturnBadRequest() throws Exception {
    LocalDateTime startDateTime = LocalDateTime.now();
    LocalDateTime endDateTime = LocalDateTime.now().minusDays(1);
    performRequest(get("/api/audit-logs/statistics")
      .param("startDateTime", startDateTime.toString())
      .param("endDateTime", endDateTime.toString()))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("End date-time cannot be before start date-time"));
  }
  
  private AuditLog createTestAuditLog(String operation) {
    return new AuditLog(
      TEST_USER_EMAIL,
      "testMethod",
      operation,
      LocalDateTime.now(),
      100L,
      "/api/test",
      "GET"
    );
  }
  
  private AuditLogResponseDTO createTestAuditLogDTO(Long id, String operation) {
    return new AuditLogResponseDTO(
      id,
      TEST_USER_EMAIL,
      "testMethod",
      operation,
      LocalDateTime.now(),
      100L,
      "/api/test",
      "GET"
    );
  }
}