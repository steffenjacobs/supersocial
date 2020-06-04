package me.steffenjacobs.supersocial.security.exception;

/**
 * Should be thrown during the user registration process if a user with the
 * given username already exists.
 * 
 * @author Steffen Jacobs
 */
public class UserAlreadyExistsException extends RuntimeException {
	private static final long serialVersionUID = 3521459427431959300L;

	public UserAlreadyExistsException(String username) {
		super(String.format("User '%s' already exists. Please choose a different username.", username));
	}

}
