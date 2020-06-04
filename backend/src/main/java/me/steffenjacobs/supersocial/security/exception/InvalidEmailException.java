package me.steffenjacobs.supersocial.security.exception;

/**
 * Should be thrown during the user registration process no or an invalid email
 * was given.
 * 
 * @author Steffen Jacobs
 */
public class InvalidEmailException extends RuntimeException {
	private static final long serialVersionUID = -3695732047178736195L;

	public InvalidEmailException(String email) {
		super(String.format("Invalid email '%s'. Please choose a different email.", email));
	}
}
