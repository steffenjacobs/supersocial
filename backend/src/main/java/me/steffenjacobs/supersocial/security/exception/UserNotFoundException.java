package me.steffenjacobs.supersocial.security.exception;

import java.util.UUID;

/**
 * Should be thrown if a user with the given id does not exist.
 * 
 * @author Steffen Jacobs
 */
public class UserNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 2887029609387431553L;

	public UserNotFoundException(UUID id) {
		super(String.format("User with id '%s' does not exist.", id.toString()));
	}

}
