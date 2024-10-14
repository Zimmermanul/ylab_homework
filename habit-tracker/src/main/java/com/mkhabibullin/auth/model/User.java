package com.mkhabibullin.auth.model;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

/**
 * A model class that describes a user.
 */
public class User implements Serializable {
  private final String id;
  private String email;
  private String passwordHash;
  private String salt;
  private String name;
  private boolean isAdmin;
  private boolean isBlocked;
  
  public User(String email, String name) {
    this(UUID.randomUUID().toString(), email, name);
  }
  public User(String id, String email, String name) {
    this.id = id;
    this.email = email;
    this.name = name;
    this.isAdmin = false;
    this.isBlocked = false;
  }
  public String getId() {
    return id;
  }
  public String getEmail() {
    return email;
  }
  
  public void setEmail(String email) {
    this.email = email;
  }
  
  public String getPasswordHash() {
    return passwordHash;
  }
  
  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }
  
  public String getSalt() {
    return salt;
  }
  
  public void setSalt(String salt) {
    this.salt = salt;
  }
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setPassword(String password) {
    this.salt = generateSalt();
    this.passwordHash = hashPassword(password, this.salt);
  }
  
  public boolean isAdmin() {
    return isAdmin;
  }
  
  public void setAdmin(boolean admin) {
    isAdmin = admin;
  }
  
  public boolean isBlocked() {
    return isBlocked;
  }
  
  public void setBlocked(boolean blocked) {
    isBlocked = blocked;
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
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(id, user.id);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}