package org.ollide.spring.discourse.sso;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discourse.sso")
public class DiscourseSsoProperties {

    private static final String DEFAULT_RETURN_PATH = "/login/discourse/success";

    private String discourseUrl;
    private String secret;
    private String returnPath = DEFAULT_RETURN_PATH;

    public String getDiscourseUrl() {
        return discourseUrl;
    }

    public void setDiscourseUrl(String discourseUrl) {
        this.discourseUrl = discourseUrl;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getReturnPath() {
        return returnPath;
    }

    public void setReturnPath(String returnPath) {
        this.returnPath = returnPath;
    }

}
