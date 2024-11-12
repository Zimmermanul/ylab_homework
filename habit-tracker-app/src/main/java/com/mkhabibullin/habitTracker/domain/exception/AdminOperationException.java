package com.mkhabibullin.habitTracker.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when attempting to perform restricted operations
 * on administrator accounts. This includes operations such as:
 * <ul>
 *   <li>Blocking an admin account</li>
 *   <li>Unblocking an admin account</li>
 *   <li>Deleting an admin account</li>
 * </ul>
 * This exception helps protect administrator accounts from unauthorized modifications.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AdminOperationException extends RuntimeException {
  public AdminOperationException(String message) {
    super(message);
  }
}