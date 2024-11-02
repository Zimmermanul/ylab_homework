package com.mkhabibullin.application.validation;

import com.mkhabibullin.domain.exception.AuthenticationException;
import com.mkhabibullin.domain.exception.AuthorizationException;
import com.mkhabibullin.domain.model.User;
import jakarta.servlet.http.HttpSession;
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
   * @throws AuthenticationException if user is not authenticated
   */
  public User validateAuthentication() throws AuthenticationException {
    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attr == null) {
      throw new AuthenticationException("No request context found");
    }
    
    HttpSession session = attr.getRequest().getSession(false);
    User user = session != null ? (User) session.getAttribute("user") : null;
    
    if (user == null) {
      throw new AuthenticationException("User not authenticated");
    }
    
    return user;
  }
  
  /**
   * Validates if the user has admin privileges
   *
   * @param user User to validate
   * @throws AuthorizationException if user is not an admin
   */
  public void validateAdminPrivileges(User user) throws AuthorizationException {
    if (user == null || !user.isAdmin()) {
      throw new AuthorizationException("Admin privileges required");
    }
  }
  
  /**
   * Validates if the user has permission to modify the specified user account
   *
   * @param currentUser     Currently authenticated user
   * @param targetUserEmail Email of the user being modified
   * @throws AuthorizationException if user doesn't have sufficient privileges
   */
  public void validateModificationPermission(User currentUser, String targetUserEmail)
    throws AuthorizationException {
    if (currentUser == null) {
      throw new AuthorizationException("User not authenticated");
    }
    
    if (!currentUser.isAdmin() && !currentUser.getEmail().equals(targetUserEmail)) {
      throw new AuthorizationException("Insufficient privileges to modify user: " + targetUserEmail);
    }
  }
  
  /**
   * Validates user session and ensures it's active
   *
   * @param session HTTP session to validate
   * @throws AuthenticationException if session is invalid or expired
   */
  public void validateSession(HttpSession session) throws AuthenticationException {
    if (session == null || session.getAttribute("user") == null) {
      throw new AuthenticationException("Invalid or expired session");
    }
  }
}