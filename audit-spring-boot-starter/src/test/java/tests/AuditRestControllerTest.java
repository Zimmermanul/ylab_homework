package tests;

import com.mkhabibullin.audit.application.mapper.AuditMapper;
import com.mkhabibullin.audit.application.service.AuditLogService;
import com.mkhabibullin.audit.application.validation.AuditValidator;
import com.mkhabibullin.audit.domain.model.AuditLog;
import com.mkhabibullin.audit.domain.model.AuditStatistics;
import com.mkhabibullin.audit.presentation.AuditRestController;
import com.mkhabibullin.audit.presentation.dto.AuditLogResponseDTO;
import com.mkhabibullin.audit.presentation.dto.AuditStatisticsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuditRestControllerTest {
  
  @Mock
  private AuditLogService auditLogService;
  @Mock
  private AuditMapper auditMapper;
  @Mock
  private AuditValidator auditValidator;
  
  private MockMvc mockMvc;
  private AuditRestController auditController;
  
  protected static final String TEST_USER_EMAIL = "test@example.com";
  
  @BeforeEach
  void setUp() {
    auditController = new AuditRestController(auditLogService, auditMapper, auditValidator);
    mockMvc = MockMvcBuilders
      .standaloneSetup(auditController)
      .build();
  }
  
  @Test
  @DisplayName("Get recent logs should return logs")
  @WithMockUser(username = TEST_USER_EMAIL)
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
      .param("limit", String.valueOf(limit))
      .with(user(TEST_USER_EMAIL)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].operation").value("User Login"))
      .andExpect(jsonPath("$[1].operation").value("Create Habit"));
    verify(auditLogService).getRecentLogs(limit);
    verify(auditMapper).auditLogsToResponseDtos(auditLogs);
  }
  
  @Test
  @DisplayName("Get recent logs with invalid limit should return bad request")
  @WithMockUser(username = TEST_USER_EMAIL)
  void getRecentLogsWithInvalidLimitShouldReturnBadRequest() throws Exception {
    performRequest(get("/api/audit-logs/recent")
      .param("limit", "0")
      .with(user(TEST_USER_EMAIL)))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.message").value("Limit must be between 1 and 100"));
  }
  
  @Test
  @DisplayName("Get user logs should return user-specific logs")
  @WithMockUser(username = TEST_USER_EMAIL)
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
    performRequest(get("/api/audit-logs/user/{username}", username)
      .with(user(TEST_USER_EMAIL)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$").isArray())
      .andExpect(jsonPath("$.length()").value(2))
      .andExpect(jsonPath("$[0].operation").value("View Habits"));
    verify(auditLogService).getUserLogs(username);
    verify(auditMapper).auditLogsToResponseDtos(auditLogs);
  }
  
  @Test
  @DisplayName("Get statistics should return audit statistics")
  @WithMockUser(username = TEST_USER_EMAIL)
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
      8L, 135.0, operationCounts, userActivityCounts,
      averageTimeByOperation, "user1", "Create Habit",
      startDateTime, endDateTime
    );
    AuditStatisticsDTO statisticsDTO = new AuditStatisticsDTO(
      8L, 135.0, operationCounts, userActivityCounts,
      averageTimeByOperation, "user1", "Create Habit",
      startDateTime, endDateTime
    );
    given(auditLogService.getStatistics(startDateTime, endDateTime))
      .willReturn(statistics);
    given(auditMapper.statisticsToDto(statistics))
      .willReturn(statisticsDTO);
    performRequest(get("/api/audit-logs/statistics")
      .param("startDateTime", startDateTime.toString())
      .param("endDateTime", endDateTime.toString())
      .with(user(TEST_USER_EMAIL)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.totalOperations").value(8))
      .andExpect(jsonPath("$.averageExecutionTime").value(135.0))
      .andExpect(jsonPath("$.mostActiveUser").value("user1"))
      .andExpect(jsonPath("$.mostCommonOperation").value("Create Habit"));
    verify(auditLogService).getStatistics(startDateTime, endDateTime);
    verify(auditMapper).statisticsToDto(statistics);
  }
  
  private ResultActions performRequest(MockHttpServletRequestBuilder builder) throws Exception {
    return mockMvc.perform(builder
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON));
  }
  
  private AuditLog createTestAuditLog(String operation) {
    AuditLog auditLog = new AuditLog();
    auditLog.setUsername(TEST_USER_EMAIL);
    auditLog.setMethodName("testMethod");
    auditLog.setOperation(operation);
    auditLog.setTimestamp(LocalDateTime.now());
    auditLog.setExecutionTimeMs(100L);
    auditLog.setRequestUri("/api/test");
    auditLog.setRequestMethod("GET");
    return auditLog;
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