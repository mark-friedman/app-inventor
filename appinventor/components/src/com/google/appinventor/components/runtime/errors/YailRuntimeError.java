// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.appinventor.components.runtime.errors;

/**
 * Runtime error not fitting in any more specific category.
 *
 * @author halabelson@google.com (Hal Abelson)
 */
public class YailRuntimeError extends RuntimeError {

  private String errorType;

  public YailRuntimeError(String message, String errorType) {
    super(message);
    this.errorType = errorType;
  }

  public String getErrorType() {
    return errorType;
  }

}
