package me.steffenjacobs.supersocial.persistence.exception;

import java.util.UUID;

/** @author Steffen Jacobs */
public class CredentialNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 7227763219661059584L;

	public CredentialNotFoundException(UUID id) {
		super(String.format("Credential with id '%s' was not found.", id));
	}
}
