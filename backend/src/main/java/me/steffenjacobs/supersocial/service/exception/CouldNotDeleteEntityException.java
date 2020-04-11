package me.steffenjacobs.supersocial.service.exception;

/**
 * Should be fired if entities (e.g. a
 * {@link me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount} could
 * not be deleted.
 * 
 * @author Steffen Jacobs
 */
public class CouldNotDeleteEntityException extends RuntimeException {
	private static final long serialVersionUID = -2373539404814798218L;

	public CouldNotDeleteEntityException(String message) {
		super(message);
	}

}
