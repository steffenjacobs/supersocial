package me.steffenjacobs.supersocial.persistence.exception;

import java.util.UUID;

/**
 * Should be fired if a post is to be scheduled that is already scheduled to be
 * published.
 * 
 * @author Steffen Jacobs
 */
public class PostAlreadyScheduledException extends RuntimeException {
	private static final long serialVersionUID = -458410525725969107L;

	public PostAlreadyScheduledException(UUID postId) {
		super(String.format("Post '%s' is already scheduled to be published.", postId));
	}
}
