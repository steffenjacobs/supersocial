package me.steffenjacobs.supersocial.service.exception;

/** @author Steffen Jacobs */
public class TwitterException extends RuntimeException {
	private static final long serialVersionUID = -3307124549745097662L;

	public TwitterException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public TwitterException(String msg) {
		super(msg);
	}
}
