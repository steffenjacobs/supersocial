package me.steffenjacobs.supersocial.persistence.exception;

/** @author Steffen Jacobs */
public class CredentialNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 7227763219661059584L;

	public CredentialNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public CredentialNotFoundException(String msg) {
		super(msg);
	}
}
