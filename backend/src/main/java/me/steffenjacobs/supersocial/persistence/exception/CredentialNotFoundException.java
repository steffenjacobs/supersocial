package me.steffenjacobs.supersocial.persistence.exception;

import java.util.UUID;

/**
 * Should be fired if the credentials required to perform an action on a social
 * media platform do not exist.
 * 
 * @author Steffen Jacobs
 */
public class CredentialNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 7227763219661059584L;

	public CredentialNotFoundException(UUID id) {
		super(String.format("Credential with id '%s' was not found.", id));
	}
}
