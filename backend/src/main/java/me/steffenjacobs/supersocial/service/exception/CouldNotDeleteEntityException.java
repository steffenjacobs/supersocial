package me.steffenjacobs.supersocial.service.exception;

/** @author Steffen Jacobs */
public class CouldNotDeleteEntityException extends RuntimeException {
	private static final long serialVersionUID = -2373539404814798218L;

	public CouldNotDeleteEntityException(String message) {
		super(message);
	}

}
