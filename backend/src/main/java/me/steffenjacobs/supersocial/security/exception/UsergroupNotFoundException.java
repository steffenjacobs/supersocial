package me.steffenjacobs.supersocial.security.exception;

import java.util.UUID;

/**
 * Should be thrown if a user group with the given identifier does not exist.
 * 
 * @author Steffen Jacobs
 */
public class UsergroupNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 67814387431136195L;

	public UsergroupNotFoundException(UUID id) {
		super(String.format("Usergroup with id '%s' does not exist.", id.toString()));
	}

}
