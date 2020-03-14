package me.steffenjacobs.supersocial.service.exception;

/** @author Steffen Jacobs */
public class PostNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -1259114730425935604L;

	public PostNotFoundException(long postId) {
		super(String.format("Post '%s' not found.", postId));
	}

}
