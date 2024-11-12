package com.mkhabibullin.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark methods for audit logging.
 * Methods annotated with @Audited will have their execution details logged,
 * including timing, user information, and the specified audit operation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Audited {
  String audited();
}