package me.steffenjacobs.supersocial.service.exception;

import java.util.UUID;

/**
 * Should be fired if the selected
 * {@link me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount} does
 * not exist or the current user is not permitted to use it.@author Steffen
 * Jacobs
 */
public class SocialMediaAccountNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 7315515060744219562L;

	public SocialMediaAccountNotFoundException(UUID id) {
		super(String.format("No social media account '%s' found with the required permission for this operation.", id));
	}

}
