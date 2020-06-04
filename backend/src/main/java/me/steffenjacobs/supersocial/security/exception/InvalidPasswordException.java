package me.steffenjacobs.supersocial.security.exception;

/**
 * Should be thrown during the user registration process no or an invalid
 * password was given.
 * 
 * @author Steffen Jacobs
 */
public class InvalidPasswordException extends RuntimeException {
	private static final long serialVersionUID = 5725483010241513232L;

	public InvalidPasswordException() {
		super("Password could not be processed. Please choose a different password.");
	}
}
