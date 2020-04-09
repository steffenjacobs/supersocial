package me.steffenjacobs.supersocial.service.exception;

/** @author Steffen Jacobs */
public class FacebookPostNotFoundException extends FacebookException {
	private static final long serialVersionUID = -1433386750230354687L;

	public FacebookPostNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public FacebookPostNotFoundException(String msg) {
		super(msg);
	}
}
