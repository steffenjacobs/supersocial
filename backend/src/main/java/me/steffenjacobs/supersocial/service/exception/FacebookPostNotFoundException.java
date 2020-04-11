package me.steffenjacobs.supersocial.service.exception;

/**
 * Should be fired if a given facebook post is no longer available on the social
 * network.
 * 
 * @author Steffen Jacobs
 */
public class FacebookPostNotFoundException extends FacebookException {
	private static final long serialVersionUID = -1433386750230354687L;

	public FacebookPostNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public FacebookPostNotFoundException(String msg) {
		super(msg);
	}
}
