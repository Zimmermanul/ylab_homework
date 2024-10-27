package com.mkhabibullin.app.mapper;

import com.mkhabibullin.app.dto.user.RegisterUserDTO;
import com.mkhabibullin.app.dto.user.UserResponseDTO;
import com.mkhabibullin.app.model.User;
import com.mkhabibullin.app.validation.UserMapperValidator;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "default", uses = UserMapperValidator.class)
public interface UserMapper {
  UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
  
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "passwordHash", ignore = true)
  @Mapping(target = "salt", ignore = true)
  @Mapping(target = "admin", constant = "false")
  @Mapping(target = "blocked", constant = "false")
  User registerDtoToUser(RegisterUserDTO dto);
  
  @Mapping(target = "isAdmin", source = "admin")
  @Mapping(target = "isBlocked", source = "blocked")
  UserResponseDTO userToResponseDto(User user);
}