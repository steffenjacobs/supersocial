package me.steffenjacobs.supersocial.security.exception;

/**
 * Should be thrown during the user registration process no or an invalid
 * username was given.
 * 
 * @author Steffen Jacobs
 */
public class InvalidUsernameException extends RuntimeException {
	private static final long serialVersionUID = -4132318578424179644L;

	public InvalidUsernameException(String username) {
		super(String.format("Username '%s' could not be processed. Please choose a different username.", username));
	}
}
