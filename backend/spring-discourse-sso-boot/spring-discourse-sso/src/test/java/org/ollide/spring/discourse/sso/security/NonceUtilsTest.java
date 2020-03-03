package org.ollide.spring.discourse.sso.security;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NonceUtilsTest {

    @Test
    public void createNonce() {
        String nonce = NonceUtils.createNonce();
        assertTrue(NonceUtils.isValid(nonce));

        assertFalse(NonceUtils.isValid("123237462187346:25ae52fc332"));
    }

}
