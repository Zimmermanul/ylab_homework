package com.mkhabibullin.domain.model;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

/**
 * Represents a user in the system.
 * This class encapsulates all the information related to a single user, including
 * authentication details and user status.
 * It implements Serializable to allow for easy saving and transmission of user objects.
 */
public class User implements Serializable {
  private Long id;
  private String email;
  private String passwordHash;
  private String salt;
  private String name;
  private boolean admin;
  private boolean blocked;
  
  /**
   * Constructs a new User with the given email, and name.
   * @param email the user's email address
   * @param name  the user's name
   */
  public User(String email, String name) {
    this.email = email;
    this.name = name;
    this.admin = false;
    this.blocked = false;
  }
  
  /**
   * Gets the unique identifier of the user.
   *
   * @return the user's ID
   */
  public Long getId() {
    return id;
  }
  
  /**
   * Gets the unique identifier of the user.
   *
   * @return the user's ID
   */
  public void setId(Long id) {
    this.id = id;
  }
  
  /**
   * Gets the email address of the user.
   *
   * @return the user's email address
   */
  public String getEmail() {
    return email;
  }
  
  /**
   * Sets the email address of the user.
   *
   * @param email the email address to set
   */
  public void setEmail(String email) {
    this.email = email;
  }
  
  /**
   * Gets the hashed password of the user.
   *
   * @return the user's hashed password
   */
  public String getPasswordHash() {
    return passwordHash;
  }
  
  /**
   * Sets the hashed password of the user.
   *
   * @param passwordHash the hashed password to set
   */
  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }
  
  /**
   * Gets the salt used for password hashing.
   *
   * @return the salt
   */
  public String getSalt() {
    return salt;
  }
  
  /**
   * Sets the salt used for password hashing.
   *
   * @param salt the salt to set
   */
  public void setSalt(String salt) {
    this.salt = salt;
  }
  
  /**
   * Gets the name of the user.
   *
   * @return the user's name
   */
  public String getName() {
    return name;
  }
  
  /**
   * Sets the name of the user.
   *
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }
  
  /**
   * Sets the password for the user.
   * This method generates a new salt and hashes the password before storing it.
   *
   * @param password the plain text password to set
   */
  public void setPassword(String password) {
    this.salt = generateSalt();
    this.passwordHash = hashPassword(password, this.salt);
  }
  
  /**
   * Checks if the user has administrative privileges.
   *
   * @return true if the user is an admin, false otherwise
   */
  public boolean isAdmin() {
    return admin;
  }
  
  /**
   * Sets the administrative status of the user.
   *
   * @param admin the admin status to set
   */
  public void setAdmin(boolean admin) {
    this.admin = admin;
  }
  
  /**
   * Checks if the user's account is blocked.
   *
   * @return true if the user is blocked, false otherwise
   */
  public boolean isBlocked() {
    return blocked;
  }
  
  /**
   * Sets the blocked status of the user's account.
   *
   * @param blocked the blocked status to set
   */
  public void setBlocked(boolean blocked) {
    this.blocked = blocked;
  }
  private String generateSalt() {
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    random.nextBytes(salt);
    return Base64.getEncoder().encodeToString(salt);
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
  
  /**
   * Compares this user to another object for equality.
   * Two users are considered equal if they have the same id.
   *
   * @param o the object to compare with
   * @return true if the objects are equal, false otherwise
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(id, user.id);
  }
  
  /**
   * Generates a hash code for this user.
   *
   * @return the hash code
   */
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
