package me.steffenjacobs.supersocial.persistence.exception;

import java.util.UUID;

/** @author Steffen Jacobs */
public class PostAlreadyScheduledException extends RuntimeException {
	private static final long serialVersionUID = -458410525725969107L;

	public PostAlreadyScheduledException(UUID postId) {
		super(String.format("Post '%s' is already scheduled to be published.", postId));
	}
}
