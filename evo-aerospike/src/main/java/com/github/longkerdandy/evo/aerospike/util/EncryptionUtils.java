package com.github.longkerdandy.evo.aerospike.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Encryption Utils
 */
public class EncryptionUtils {

    protected static final String SALT = "4e11750fa75d9b9cb6bc310c0926cf56a1fa66b7e1b0a12d2c569bad5ff4fd483d827dba2abbfee814b9eac5e8dee3bdcbfe129817a0f3e8d2d5e601c25afd8f";
    protected static final int ITERATIONS = 1024;
    protected static final int LENGTH = 128;

    private static final Logger logger = LoggerFactory.getLogger(EncryptionUtils.class);

    static {
        try {
            SALT_HEX = stringToHex(SALT);
        } catch (DecoderException e) {
            logger.error("Prepare PBKDF2 Salt with error: ", ExceptionUtils.getMessage(e));
        }
    }

    protected static byte[] SALT_HEX;

    private EncryptionUtils() {
    }

    /**
     * Encode user's password
     *
     * @param password User Password
     * @return Encoded Password
     */
    public static String encryptPassword(String password) {
        try {
            final PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), SALT_HEX, ITERATIONS, LENGTH);
            final SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            final byte[] hash = skf.generateSecret(spec).getEncoded();
            return hexToString(hash);
        } catch (NoSuchAlgorithmException e) {
            // Not gonna happen
        } catch (InvalidKeySpecException e) {
            logger.error("Encrypt user's password with error: ", ExceptionUtils.getMessage(e));
        }
        return null;
    }

    /**
     * String to Byte[]
     *
     * @param str String
     * @return Byte[]
     */
    protected static byte[] stringToHex(final String str) throws DecoderException {
        return Hex.decodeHex(str.toCharArray());
    }

    /**
     * Byte[] to String
     *
     * @param hex Byte[]
     * @return String
     */
    protected static String hexToString(final byte[] hex) {
        return Hex.encodeHexString(hex);
    }
}
