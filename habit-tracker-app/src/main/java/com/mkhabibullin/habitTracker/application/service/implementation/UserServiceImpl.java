package com.mkhabibullin.habitTracker.application.service.implementation;

import com.mkhabibullin.habitTracker.application.service.UserService;
import com.mkhabibullin.habitTracker.common.MessageConstants;
import com.mkhabibullin.habitTracker.domain.exception.AdminOperationException;
import com.mkhabibullin.habitTracker.domain.exception.DuplicateEmailException;
import com.mkhabibullin.habitTracker.domain.exception.InvalidEmailException;
import com.mkhabibullin.habitTracker.domain.exception.UserNotFoundException;
import com.mkhabibullin.habitTracker.domain.model.User;
import com.mkhabibullin.habitTracker.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

/**
 * Implementation of UserService that provides user management functionality.
 * This class handles user operations including registration, authentication,
 * profile management, and administrative tasks.
 */
@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
  
  private final UserRepository userRepository;
  
  /**
   * Retrieves a user by their ID.
   *
   * @param id the ID of the user
   * @return the User object if found, null otherwise
   */
  @Override
  public User getById(Long id) {
    return userRepository.readUserById(id);
  }
  
  /**
   * Retrieves a user by their email address.
   *
   * @param email the email address of the user
   * @return the User object if found, null otherwise
   */
  @Override
  public User getByEmail(String email) {
    return userRepository.readUserByEmail(email);
  }
  
  /**
   * Creates a new user in the system.
   *
   * @param user the User object to be created
   */
  @Override
  @Transactional
  public void create(User user) {
    if (!isValidEmail(user.getEmail())) {
      throw new InvalidEmailException(MessageConstants.EMAIL_INVALID);
    }
    if (userRepository.readUserByEmail(user.getEmail()) != null) {
      throw new DuplicateEmailException(MessageConstants.EMAIL_ALREADY_IN_USE);
    }
    userRepository.createUser(user);
  }
  
  /**
   * Retrieves a list of all users in the system.
   *
   * @return a List of all User objects
   */
  @Override
  public List<User> getAll() {
    return userRepository.getAllUsers();
  }
  
  /**
   * Blocks a user account.
   *
   * @param email the email address of the user to be blocked
   */
  @Override
  @Transactional
  public void block(String email) {
    if (!isValidEmail(email)) {
      throw new InvalidEmailException(MessageConstants.EMAIL_INVALID);
    }
    User user = userRepository.readUserByEmail(email);
    if (user == null) {
      throw new UserNotFoundException(MessageConstants.USER_NOT_FOUND);
    }
    if (user.isAdmin()) {
      throw new AdminOperationException(MessageConstants.ADMIN_USER_CANNOT_BE_MANAGED);
    }
    user.setBlocked(true);
    userRepository.updateUser(user);
  }
  
  /**
   * Unblocks a user account.
   *
   * @param email the email address of the user to be unblocked
   */
  @Override
  @Transactional
  public void unblock(String email) {
    if (!isValidEmail(email)) {
      throw new InvalidEmailException(MessageConstants.EMAIL_INVALID);
    }
    User user = userRepository.readUserByEmail(email);
    if (user == null) {
      throw new UserNotFoundException(MessageConstants.USER_NOT_FOUND);
    }
    if (user.isAdmin()) {
      throw new AdminOperationException(MessageConstants.ADMIN_USER_CANNOT_BE_MANAGED);
    }
    user.setBlocked(false);
    userRepository.updateUser(user);
  }
  
  /**
   * Registers a new user in the system.
   *
   * @param email    the email address of the new user
   * @param password the password for the new user
   * @param name     the name of the new user
   */
  @Override
  @Transactional
  public void register(String email, String password, String name) {
    User newUser = new User(email, name);
    newUser.setPassword(password);
    create(newUser);
  }
  
  /**
   * Authenticates a user.
   *
   * @param email    the email address of the user
   * @param password the password to verify
   * @return true if authentication is successful, false otherwise
   */
  @Override
  public boolean authenticate(String email, String password) {
    if (!isValidEmail(email)) {
      return false;
    }
    User user = userRepository.readUserByEmail(email);
    return user != null && verifyPassword(password, user);
  }
  
  /**
   * Deletes a user account from the system.
   *
   * @param email the email address of the user to be deleted
   */
  @Override
  @Transactional
  public void deleteAccount(String email) {
    if (!isValidEmail(email)) {
      throw new InvalidEmailException(MessageConstants.EMAIL_INVALID);
    }
    User user = userRepository.readUserByEmail(email);
    if (user == null) {
      throw new UserNotFoundException(MessageConstants.USER_NOT_FOUND);
    }
    if (user.isAdmin()) {
      throw new AdminOperationException(MessageConstants.ADMIN_USER_CANNOT_BE_MANAGED);
    }
    userRepository.deleteUser(email);
  }
  
  /**
   * Updates the email address of a user.
   *
   * @param userId   the ID of the user
   * @param newEmail the new email address
   */
  @Override
  @Transactional
  public void updateEmail(Long userId, String newEmail) {
    if (!isValidEmail(newEmail)) {
      throw new InvalidEmailException("Invalid email format");
    }
    User user = userRepository.readUserById(userId);
    if (user == null) {
      throw new UserNotFoundException(MessageConstants.USER_NOT_FOUND);
    }
    if (userRepository.readUserByEmail(newEmail) != null) {
      throw new DuplicateEmailException(MessageConstants.EMAIL_ALREADY_IN_USE);
    }
    user.setEmail(newEmail);
    userRepository.updateUser(user);
  }
  
  /**
   * Updates the name of a user.
   *
   * @param userId  the ID of the user
   * @param newName the new name
   */
  @Override
  @Transactional
  public void updateName(Long userId, String newName) {
    User user = userRepository.readUserById(userId);
    if (user == null) {
      throw new UserNotFoundException(MessageConstants.USER_NOT_FOUND);
    }
    user.setName(newName);
    userRepository.updateUser(user);
  }
  
  /**
   * Updates the password of a user.
   *
   * @param userId      the ID of the user
   * @param newPassword the new password
   */
  @Override
  @Transactional
  public void updatePassword(Long userId, String newPassword) {
    User user = userRepository.readUserById(userId);
    if (user == null) {
      throw new UserNotFoundException(MessageConstants.USER_NOT_FOUND);
    }
    user.setPassword(newPassword);
    userRepository.updateUser(user);
  }
  
  private boolean isValidEmail(String email) {
    return email != null && EMAIL_PATTERN.matcher(email).matches();
  }
  
  private boolean verifyPassword(String inputPassword, User user) {
    String hashedInputPassword = hashPassword(inputPassword, user.getSalt());
    return hashedInputPassword.equals(user.getPasswordHash());
  }
  
  private String hashPassword(String password, String salt) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      md.update(Base64.getDecoder().decode(salt));
      byte[] hashedPassword = md.digest(password.getBytes());
      return Base64.getEncoder().encodeToString(hashedPassword);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error hashing password", e);
    }
  }
}