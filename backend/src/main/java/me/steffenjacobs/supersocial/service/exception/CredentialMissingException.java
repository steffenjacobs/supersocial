package me.steffenjacobs.supersocial.service.exception;

import me.steffenjacobs.supersocial.persistence.CredentialPersistenceManager.CredentialType;

/** @author Steffen Jacobs */
public class CredentialMissingException extends RuntimeException {
	private static final long serialVersionUID = -1334184885295892310L;

	public CredentialMissingException(CredentialType credential) {
		super(String.format("No credentials found for %s", credential.getKey()));
	}
}
