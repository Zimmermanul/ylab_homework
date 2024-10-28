package com.mkhabibullin.app.infrastructure.config;

import com.mkhabibullin.app.application.service.HabitExecutionService;
import com.mkhabibullin.app.application.service.HabitService;
import com.mkhabibullin.app.application.service.UserService;
import com.mkhabibullin.app.infrastructure.persistence.repository.HabitDbRepository;
import com.mkhabibullin.app.infrastructure.persistence.repository.HabitExecutionDbRepository;
import com.mkhabibullin.app.infrastructure.persistence.repository.UserDbRepository;
import com.mkhabibullin.app.presentation.controller.HabitController;
import com.mkhabibullin.app.presentation.controller.HabitExecutionController;
import com.mkhabibullin.app.presentation.controller.UserController;

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
  private UserService userService;
  private HabitService habitService;
  private HabitExecutionService executionService;
  private UserController userController;
  private HabitController habitController;
  private HabitExecutionController executionController;
  
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
    
    this.userService = new UserService(userRepository);
    this.habitService = new HabitService(habitRepository, userRepository);
    this.executionService = new HabitExecutionService(executionRepository, habitRepository);
    
    this.userController = new UserController(userService);
    this.habitController = new HabitController(habitService);
    this.executionController = new HabitExecutionController(executionService);
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
}