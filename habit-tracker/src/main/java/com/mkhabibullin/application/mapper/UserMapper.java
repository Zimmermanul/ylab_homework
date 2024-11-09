package com.mkhabibullin.application.mapper;

import com.mkhabibullin.domain.model.User;
import com.mkhabibullin.presentation.dto.user.RegisterUserDTO;
import com.mkhabibullin.presentation.dto.user.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper interface for converting between User entities and DTOs.
 * Provides mapping functionality for user-related data transformations.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
  
  /**
   * Converts a registration DTO to a user entity.
   * Creates a new user entity with default security settings.
   *
   * @param dto the registration DTO
   * @return the mapped User entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "passwordHash", ignore = true)
  @Mapping(target = "salt", ignore = true)
  @Mapping(target = "admin", constant = "false")
  @Mapping(target = "blocked", constant = "false")
  User registerDtoToUser(RegisterUserDTO dto);
  
  /**
   * Converts a user entity to a response DTO.
   * Creates a safe representation of user data for client responses.
   *
   * @param user the User entity
   * @return the mapped UserResponseDTO
   */
  @Mapping(target = "isAdmin", source = "admin")
  @Mapping(target = "isBlocked", source = "blocked")
  UserResponseDTO userToResponseDto(User user);
  
  /**
   * Converts a list of users to response DTOs.
   *
   * @param users list of User entities
   * @return list of UserResponseDTOs
   */
  List<UserResponseDTO> usersToResponseDtos(List<User> users);
}