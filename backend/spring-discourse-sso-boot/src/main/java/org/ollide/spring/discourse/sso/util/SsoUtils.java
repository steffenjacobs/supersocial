package org.ollide.spring.discourse.sso.util;

import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public abstract class SsoUtils {

    public static final String ADMIN = "admin";
    public static final String MODERATOR = "moderator";

    public static final String EXTERNAL_ID = "external_id";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String NAME = "name";
    public static final String AVATAR_URL = "avatar_url";
    public static final String GROUPS = "groups";

    public static final String NONCE = "nonce";
    public static final String RETURN_SSO_URL = "return_sso_url";

    /**
     * Extract a map from a query string.
     *
     * @param query a query (or fragment) string from a URI
     * @return a Map of the values in the query
     */
    public static Map<String, String> extractMap(String query) {
        Map<String, String> map = new HashMap<>();
        Properties properties = StringUtils.splitArrayElementsIntoProperties(
                StringUtils.delimitedListToStringArray(query, "&"), "=");
        if (properties != null) {
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String value = StringUtils.uriDecode(entry.getValue().toString(), StandardCharsets.UTF_8);
                map.put(entry.getKey().toString(), value);
            }
        }
        return map;
    }
}
