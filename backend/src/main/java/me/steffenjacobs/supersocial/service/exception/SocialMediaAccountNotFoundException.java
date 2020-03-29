package me.steffenjacobs.supersocial.service.exception;

import java.util.UUID;

/** @author Steffen Jacobs */
public class SocialMediaAccountNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 7315515060744219562L;
	
	public SocialMediaAccountNotFoundException(UUID id) {
		super(String.format("No social media account '%s' found with the required permission for this operation.", id));
	}

}
