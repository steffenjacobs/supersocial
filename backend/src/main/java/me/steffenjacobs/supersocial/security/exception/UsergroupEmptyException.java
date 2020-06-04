package me.steffenjacobs.supersocial.security.exception;

import java.util.UUID;

/**
 * Should be thrown if the last user had been removed from a user group and the user group is subsequently deleted.
 * 
 * @author Steffen Jacobs
 */
public class UsergroupEmptyException extends RuntimeException {
	private static final long serialVersionUID = 67814387431136195L;

	public UsergroupEmptyException(UUID id) {
		super(String.format("Usergroup with id '%s' is now empty and was deleted.", id.toString()));
	}

}
