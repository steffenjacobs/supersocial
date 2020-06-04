package me.steffenjacobs.supersocial.security.exception;

import java.util.UUID;

/**
 * Should be thrown if a user should be removed from a user group which never
 * contained the user.
 * 
 * @author Steffen Jacobs
 */
public class UserNotInUserGroupException extends RuntimeException {
	private static final long serialVersionUID = -3694838788585723097L;

	public UserNotInUserGroupException(UUID userId, UUID userGroupId) {
		super(String.format("User with id '%s' is not in user group '%s'.", userId.toString(), userGroupId.toString()));
	}

}
