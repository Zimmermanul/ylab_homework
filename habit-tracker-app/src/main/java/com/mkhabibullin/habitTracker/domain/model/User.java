package com.mkhabibullin.habitTracker.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Represents a user in a habit tracking application.
 * This class encapsulates all the information related to a single user.
 * It implements Serializable to allow for easy saving and transmission of user objects.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
@Table(name = "users", schema = "entity")
public class User implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(unique = true, nullable = false)
  private String email;
  
  @Column(name = "password_hash")
  private String passwordHash;
  
  @Column
  private String salt;
  
  @Column(nullable = false)
  private String name;
  
  @Column
  private boolean admin = false;
  
  @Column
  private boolean blocked = false;
  
  /**
   * Constructs a new User with the given email, and name.
   *
   * @param email the user's email address
   * @param name  the user's name
   */
  public User(String email, String name) {
    this.email = email;
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
}