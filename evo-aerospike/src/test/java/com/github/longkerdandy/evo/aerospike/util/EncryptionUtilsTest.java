package com.github.longkerdandy.evo.aerospike.util;

import org.apache.commons.codec.DecoderException;
import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * EncryptionUtils Test
 */
public class EncryptionUtilsTest {

    @Test
    public void saltTest() throws NoSuchAlgorithmException, DecoderException {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[64];
        sr.nextBytes(salt);
        assert salt.length == 64;

        String saltEncoded = EncryptionUtils.hexToString(salt);
        assert saltEncoded.length() == 128;
        byte[] saltDecoded = EncryptionUtils.stringToHex(saltEncoded);
        assert saltDecoded.length == 64;
        assert Arrays.equals(salt, saltDecoded);
    }

    @Test
    public void passwordTest() {
        String password1 = "1234567890";
        String password2 = "abcdefghijklmn";
        String password1Encoded = EncryptionUtils.encryptPassword(password1);
        String password2Encoded = EncryptionUtils.encryptPassword(password2);
        assert password1Encoded != null;
        assert !password1Encoded.equals(password2Encoded);
        String password1EncodedAgain = EncryptionUtils.encryptPassword(password1);
        assert password1Encoded.equals(password1EncodedAgain);
    }
}
