package com.mkhabibullin.app.validation;

import com.mkhabibullin.app.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class AuthenticationValidator {
  /**
   * Validates user authentication status from the HTTP session
   *
   * @param request HTTP request containing the session
   * @return authenticated User object
   * @throws AuthenticationException if user is not authenticated
   */
  public static User validateAuthentication(HttpServletRequest request) throws AuthenticationException {
    HttpSession session = request.getSession(false);
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
  public static void validateAdminPrivileges(User user) throws AuthorizationException {
    if (!user.isAdmin()) {
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
  public static void validateModificationPermission(User currentUser, String targetUserEmail)
    throws AuthorizationException {
    if (!currentUser.isAdmin() && !currentUser.getEmail().equals(targetUserEmail)) {
      throw new AuthorizationException("Insufficient privileges");
    }
  }
  
  public static class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
      super(message);
    }
  }
  
  public static class AuthorizationException extends Exception {
    public AuthorizationException(String message) {
      super(message);
    }
  }
}
