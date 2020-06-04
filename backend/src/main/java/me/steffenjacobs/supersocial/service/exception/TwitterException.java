package me.steffenjacobs.supersocial.service.exception;

/**
 * Generic Facebook-related exception that can be fired when interaction with
 * the Facebook-API was not successful. @author Steffen Jacobs
 */
public class TwitterException extends RuntimeException {
	private static final long serialVersionUID = -3307124549745097662L;

	public TwitterException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public TwitterException(String msg) {
		super(msg);
	}
}
