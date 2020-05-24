package me.steffenjacobs.supersocial.security.exception;

import java.util.UUID;

/**
 * Should be thrown if the default user is attempted to be deleted from the default user group with the given identifier.
 * 
 * @author Steffen Jacobs
 */
public class CouldNotDeleteDefaultUserFromDefaultUserGroup extends RuntimeException {
	private static final long serialVersionUID = 7938202482080221099L;

	public CouldNotDeleteDefaultUserFromDefaultUserGroup(UUID id) {
		super(String.format("User could not be deleted from user group '%s' because this is the user's default user group.", id.toString()));
	}

}
