package me.steffenjacobs.supersocial.persistence.exception;

import java.util.UUID;

/** @author Steffen Jacobs */
public class PostNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -1259114730425935604L;

	public PostNotFoundException(UUID postId) {
		super(String.format("Post '%s' not found.", postId));
	}

}
