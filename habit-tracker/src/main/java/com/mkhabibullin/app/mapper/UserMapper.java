package com.mkhabibullin.app.mapper;

import com.mkhabibullin.app.dto.user.RegisterUserDTO;
import com.mkhabibullin.app.dto.user.UserResponseDTO;
import com.mkhabibullin.app.model.User;
import com.mkhabibullin.app.validation.UserMapperValidator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * MapStruct mapper interface for converting between User entities and DTOs.
 * This mapper handles user data transformations while performing validation
 * through {@link UserMapperValidator}. It ensures sensitive information
 * like passwords and salts are properly handled during conversions.
 *
 * <p>The mapper is configured with:</p>
 * <ul>
 *   <li>Default component model for simple instantiation</li>
 *   <li>Integration with {@link UserMapperValidator} for field validation</li>
 *   <li>A singleton INSTANCE for stateless mapping operations</li>
 * </ul>
 *
 * @see org.mapstruct.Mapper
 * @see UserMapperValidator
 * @see User
 * @see RegisterUserDTO
 * @see UserResponseDTO
 */
@Mapper(componentModel = "default", uses = UserMapperValidator.class)
public interface UserMapper {
  /**
   * Singleton instance of the mapper.
   * Use this instance for all mapping operations to ensure consistent behavior
   * and optimal resource usage.
   */
  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
  
  /**
   * Converts a registration DTO to a user entity.
   * This method creates a new user entity with default security settings
   * while explicitly ignoring sensitive fields that should be handled separately.
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "passwordHash", ignore = true)
  @Mapping(target = "salt", ignore = true)
  @Mapping(target = "admin", constant = "false")
  @Mapping(target = "blocked", constant = "false")
  User registerDtoToUser(RegisterUserDTO dto);
  
  /**
   * Converts a user entity to a response DTO.
   * This method creates a safe representation of user data for client responses,
   * excluding sensitive information and mapping boolean flags to their proper names.
   */
  @Mapping(target = "isAdmin", source = "admin")
  @Mapping(target = "isBlocked", source = "blocked")
  UserResponseDTO userToResponseDto(User user);
}