package org.ollide.spring.discourse.sso.security;

import org.junit.Test;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

public class DiscourseSignerTest {

    private static final String SECRET = "qWJt+><n[g%suqo$gD_}Sk$dOBxG1j";

    private final DiscourseSigner signer = new DiscourseSigner(SECRET);

    @Test
    public void testSignAndVerify() {
        String payload = "this is my payload";
        String base64Payload = Base64Utils.encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        String signature = signer.sign(base64Payload);

        assertTrue(signer.verify(base64Payload, signature));
        assertFalse(signer.verify(base64Payload, signature + "="));
    }

}
