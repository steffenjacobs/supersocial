package me.steffenjacobs.supersocial.persistence.exception;

import java.util.UUID;

/**
 * Should be fired if the {@link me.steffenjacobs.supersocial.domain.entity.AccessControlList} for a given secured object could not be found
 * 
 * @author Steffen Jacobs
 */
public class AccessControlListNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -1259114730425935604L;

	public AccessControlListNotFoundException(UUID securedObjectId) {
		super(String.format("Could not find ACL for secured object '%s'.", securedObjectId));
	}

}
