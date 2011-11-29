// Copyright 2008 Google Inc. All Rights Reserved.

package com.google.appinventor.server.encryption;

import com.google.appinventor.common.testutils.TestUtils;

import junit.framework.TestCase;

import java.util.Arrays;

/**
 * Tests Keyczar encryption.
 *
 * @author kerr@google.com (Debby Wallach)
 */
public class KeyczarEncryptorTest extends TestCase {
  private Encryptor encryptor;

  public static final String KEYSTORE_ROOT_PATH = TestUtils.APP_INVENTOR_ROOT_DIR +
      "/appengine/build/war/";  // must end with a slash

  @Override
  public void setUp() {
    KeyczarEncryptor.rootPath.setForTest(KEYSTORE_ROOT_PATH);
    encryptor = new KeyczarEncryptor();
  }

  public void testEncryptionDecryption() throws Exception {
    byte[] random = new byte[1000];
    new java.util.Random().nextBytes(random);
    byte[] encrypted = encryptor.encrypt(random);
    byte[] decrypted = encryptor.decrypt(encrypted);
    assertTrue(Arrays.equals(random, decrypted));
  }

  public void testDecryptionOfNotEncrypted() throws Exception {
    byte[] random = new byte[1000];
    new java.util.Random().nextBytes(random);
    try {
      byte[] decrypted = encryptor.decrypt(random);
      fail();
    } catch (EncryptionException e) {
      // The expected behavior
      return;
    }
  }
}
