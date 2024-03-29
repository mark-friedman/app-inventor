// Copyright 2008 Google Inc. All Rights Reserved.

package com.google.appinventor.components.annotations;

import com.google.appinventor.components.common.PropertyTypeConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark properties to be visible in the ODE visual designer.
 *
 * <p>Only the setter method of the property must be marked with this
 * annotation.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DesignerProperty {
  /**
   * Determines the property editor used in the designer.
   *
   * @return  property type
   */
  String editorType() default PropertyTypeConstants.PROPERTY_TYPE_TEXT;

  /**
   * Default value of property.
   *
   * @return  default property value
   */
  String defaultValue() default "";
}
