package com.mkhabibullin.infrastructure.config;

import com.mkhabibullin.application.service.AuditLogService;
import com.mkhabibullin.application.service.HabitExecutionService;
import com.mkhabibullin.application.service.HabitService;
import com.mkhabibullin.application.service.UserService;
import com.mkhabibullin.infrastructure.persistence.repository.AuditLogDbRepository;
import com.mkhabibullin.infrastructure.persistence.repository.HabitDbRepository;
import com.mkhabibullin.infrastructure.persistence.repository.HabitExecutionDbRepository;
import com.mkhabibullin.infrastructure.persistence.repository.UserDbRepository;
import com.mkhabibullin.presentation.controller.AuditLogController;
import com.mkhabibullin.presentation.controller.HabitController;
import com.mkhabibullin.presentation.controller.HabitExecutionController;
import com.mkhabibullin.presentation.controller.UserController;

import javax.sql.DataSource;

/**
 * Configuration class responsible for initializing and managing application components.
 * This class handles the dependency injection and component lifecycle for the application,
 * including repositories, services, and controllers.
 */
public class ApplicationConfig {
  private final DataSource dataSource;
  private UserDbRepository userRepository;
  private HabitDbRepository habitRepository;
  private HabitExecutionDbRepository executionRepository;
  private AuditLogDbRepository auditLogRepository;
  private UserService userService;
  private HabitService habitService;
  private HabitExecutionService executionService;
  private AuditLogService auditLogService;
  private UserController userController;
  private HabitController habitController;
  private HabitExecutionController executionController;
  private AuditLogController auditLogController;
  
  /**
   * Constructs a new ApplicationConfig with the specified data source.
   * Initializes all application components including repositories, services,
   * and controllers with proper dependency injection.
   *
   * @param dataSource The data source to be used for database connections
   */
  public ApplicationConfig(DataSource dataSource) {
    this.dataSource = dataSource;
    initializeComponents();
  }
  
  private void initializeComponents() {
    this.userRepository = new UserDbRepository(dataSource);
    this.habitRepository = new HabitDbRepository(dataSource);
    this.executionRepository = new HabitExecutionDbRepository(dataSource);
    this.auditLogRepository = new AuditLogDbRepository(dataSource);
    
    this.userService = new UserService(userRepository);
    this.habitService = new HabitService(habitRepository, userRepository);
    this.executionService = new HabitExecutionService(executionRepository, habitRepository);
    this.auditLogService = new AuditLogService(auditLogRepository);
    
    this.userController = new UserController(userService);
    this.habitController = new HabitController(habitService);
    this.executionController = new HabitExecutionController(executionService);
    this.auditLogController = new AuditLogController(auditLogService);
  }
  
  /**
   * Retrieves the user controller instance.
   *
   * @return The configured {@link UserController} instance
   */
  public UserController getUserController() {
    return userController;
  }
  
  /**
   * Retrieves the habit controller instance.
   * @return The configured {@link HabitController} instance
   */
  public HabitController getHabitController() {
    return habitController;
  }
  
  /**
   * Retrieves the habit execution controller instance.
   * @return The configured {@link HabitExecutionController} instance
   */
  public HabitExecutionController getExecutionController() {
    return executionController;
  }
  
  /**
   * Retrieves the audit log controller instance.
   *
   * @return The configured {@link AuditLogController} instance
   */
  public AuditLogController getAuditLogController() {
    return auditLogController;
  }
}