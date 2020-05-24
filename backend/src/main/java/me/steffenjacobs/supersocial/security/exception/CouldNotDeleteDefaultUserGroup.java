package me.steffenjacobs.supersocial.security.exception;

import java.util.UUID;

/**
 * Should be thrown if a user group with the given identifier is a default user group.
 * 
 * @author Steffen Jacobs
 */
public class CouldNotDeleteDefaultUserGroup extends RuntimeException {
	private static final long serialVersionUID = 7938202482080221099L;

	public CouldNotDeleteDefaultUserGroup(UUID id) {
		super(String.format("Usergroup with id '%s' could not be deleted since it is a default user group.", id.toString()));
	}

}
