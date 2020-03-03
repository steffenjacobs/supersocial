package org.ollide.spring.discourse.sso.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class DiscourseSigner {

    private static final String HMAC_SHA256 = "HmacSHA256";

    private final Mac hmac;

    public DiscourseSigner(String secret) {
        byte[] byteKey = secret.getBytes(StandardCharsets.UTF_8);
        try {
            hmac = Mac.getInstance(HMAC_SHA256);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA256);
        try {
            hmac.init(keySpec);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public String sign(String payload) {
        byte[] macData = hmac.doFinal(payload.getBytes(StandardCharsets.US_ASCII));
        return HexUtils.bytesToHex(macData);
    }

    public boolean verify(String payload, String signature) {
        if (payload == null || signature == null ) {
            return false;
        }
        return sign(payload).equals(signature);
    }

}
