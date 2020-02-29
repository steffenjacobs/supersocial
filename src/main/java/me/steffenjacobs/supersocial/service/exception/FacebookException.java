package me.steffenjacobs.supersocial.service.exception;

/** @author Steffen Jacobs */
public class FacebookException extends RuntimeException {
	private static final long serialVersionUID = -6331699835266083272L;

	public FacebookException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public FacebookException(String msg) {
		super(msg);
	}
}
