package com.mkhabibullin.application.validation;

import com.mkhabibullin.common.MessageConstants;
import com.mkhabibullin.domain.exception.CustomAuthenticationException;
import com.mkhabibullin.domain.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Validator class for authentication and authorization checks.
 * Provides utility methods to validate user authentication status and permissions.
 */
@Component
public class AuthenticationValidator {
  /**
   * Validates user authentication status from the current session
   *
   * @return authenticated User object
   * @throws CustomAuthenticationException  if user is not authenticated
   */
  public User validateAuthentication() throws CustomAuthenticationException {
    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attr == null) {
      throw new CustomAuthenticationException(MessageConstants.NO_REQUEST_CONTEXT);
    }
    
    HttpSession session = attr.getRequest().getSession(false);
    User user = session != null ? (User) session.getAttribute("user") : null;
    
    if (user == null) {
      throw new CustomAuthenticationException(MessageConstants.USER_NOT_AUTHENTICATED);
    }
    
    return user;
  }
  
  /**
   * Validates if the user has admin privileges
   *
   * @param user User to validate
   * @throws CustomAuthenticationException  if user is not an admin
   */
  public void validateAdminPrivileges(User user) throws CustomAuthenticationException {
    if (user == null || !user.isAdmin()) {
      throw new CustomAuthenticationException(MessageConstants.ADMIN_PRIVILEGES_REQUIRED);
    }
  }
  
  /**
   * Validates if the user has permission to modify the specified user account
   *
   * @param currentUser     Currently authenticated user
   * @param targetUserEmail Email of the user being modified
   * @throws AccessDeniedException  if user doesn't have sufficient privileges
   */
  public void validateModificationPermission(User currentUser, String targetUserEmail)
    throws AccessDeniedException {
    if (currentUser == null) {
      throw new AccessDeniedException(MessageConstants.USER_NOT_AUTHENTICATED);
    }
    
    if (!currentUser.isAdmin() && !currentUser.getEmail().equals(targetUserEmail)) {
      throw new AccessDeniedException(
        String.format(MessageConstants.INSUFFICIENT_PRIVILEGES, targetUserEmail)
      );
    }
  }
  
  /**
   * Validates user session and ensures it's active
   *
   * @param session HTTP session to validate
   * @throws CustomAuthenticationException  if session is invalid or expired
   */
  public void validateSession(HttpSession session) throws CustomAuthenticationException {
    if (session == null || session.getAttribute("user") == null) {
      throw new CustomAuthenticationException(MessageConstants.INVALID_SESSION);
    }
  }
}