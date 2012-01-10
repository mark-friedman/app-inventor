// Copyright 2010 Google Inc. All Rights Reserved.

package openblocks.codeblockutil;

import java.io.IOException;

/**
 * Thrown when the specified command cannot be found
 *
 * @author halabelson@google.com (Hal Abelson)
 */
public class NoAICommandException extends IOException {

// This is commented out because it seems to destroy Java 5 compatibility
//   public NoAdbException(String message, Throwable cause) {
//     super(message, cause);
//   }

  public NoAICommandException(String message) {
    super(message);
  }

}
