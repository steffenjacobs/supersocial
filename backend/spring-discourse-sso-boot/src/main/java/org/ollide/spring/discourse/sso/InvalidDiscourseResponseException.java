package org.ollide.spring.discourse.sso;

import org.springframework.security.core.AuthenticationException;

public class InvalidDiscourseResponseException extends AuthenticationException {

    public InvalidDiscourseResponseException(String message) {
        super(message);
    }

    public InvalidDiscourseResponseException(String message, Throwable cause) {
        super(message, cause);
    }

}
