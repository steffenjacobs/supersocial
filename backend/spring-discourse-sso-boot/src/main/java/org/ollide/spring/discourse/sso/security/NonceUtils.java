package org.ollide.spring.discourse.sso.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class NonceUtils {

    private static final int NONCE_VALIDITY_SECONDS = 300;

    private static final byte[] seed;

    static {
        SecureRandom secureRandom = new SecureRandom();
        seed = secureRandom.generateSeed(8);
    }

    private NonceUtils() {
        // do not instantiate
    }

    public static String createNonce() {
        long expiryTime = System.currentTimeMillis() + (long) (NONCE_VALIDITY_SECONDS * 1000);
        String signature = sign(String.valueOf(expiryTime));
        return expiryTime + ":" + signature;
    }

    public static boolean isValid(String nonce) {
        String[] split = nonce.split(":");
        if (split.length < 2) {
            return false;
        }

        String time = split[0];
        String signature = split[1];

        if (!sign(time).equals(signature)) {
            // Invalid signature
            return false;
        }

        long expiryTime = Long.parseLong(time);
        return System.currentTimeMillis() <= expiryTime;
    }

    private static String sign(String expiryTime) {
        String nonceValue = expiryTime + ":" + new String(seed);

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        byte[] hash = digest.digest(nonceValue.getBytes(StandardCharsets.UTF_8));
        return HexUtils.bytesToHex(hash);
    }
}
