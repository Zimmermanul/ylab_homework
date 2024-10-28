package com.mkhabibullin.app.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods that require audit logging.
 * This annotation is used to track method executions, including timing and user information.
 * It can be applied to methods in servlets and services that need to be monitored.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audited {
  /**
   * The operation name or description being audited.
   * This value will be stored in the audit log to identify the type of operation performed.
   *
   * @return the operation name
   */
  String operation() default "";
}