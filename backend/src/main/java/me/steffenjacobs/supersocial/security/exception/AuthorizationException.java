package me.steffenjacobs.supersocial.security.exception;

import org.springframework.security.access.AccessDeniedException;

import me.steffenjacobs.supersocial.domain.entity.SecuredAction;

/**
 * Should be thrown if a user attempts to perform an action which is not
 * permitted for the current user.
 * 
 * @author Steffen Jacobs
 */
public class AuthorizationException extends AccessDeniedException {
	private static final long serialVersionUID = 5275432558926145865L;

	public AuthorizationException(String objectName, SecuredAction action) {
		super(String.format("For this operation, %s permission is required on %s", action.name(), objectName));
	}

}
