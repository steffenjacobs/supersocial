package me.steffenjacobs.supersocial.service.exception;

/** @author Steffen Jacobs */
public class ScheduledPostNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -9153546618705378494L;

	public ScheduledPostNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public ScheduledPostNotFoundException(String msg) {
		super(msg);
	}
}
