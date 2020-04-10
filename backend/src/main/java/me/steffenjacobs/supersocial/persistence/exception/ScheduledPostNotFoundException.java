package me.steffenjacobs.supersocial.persistence.exception;

import java.util.UUID;

/**
 * Should be fired if a scheduled post with the given UUID was not found.
 * 
 * @author Steffen Jacobs
 */
public class ScheduledPostNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -9153546618705378494L;

	public ScheduledPostNotFoundException(UUID postId) {
		super(String.format("Scheduled post '%s' not found.", postId));
	}
}
