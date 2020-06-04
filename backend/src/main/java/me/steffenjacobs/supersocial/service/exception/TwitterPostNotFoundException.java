package me.steffenjacobs.supersocial.service.exception;

/**
 * Should be fired if a given tweet is no longer available on the social
 * network.
 * 
 * @author Steffen Jacobs
 */
public class TwitterPostNotFoundException extends FacebookException {
	private static final long serialVersionUID = 81866945219486735L;

	public TwitterPostNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public TwitterPostNotFoundException(String msg) {
		super(msg);
	}
}
