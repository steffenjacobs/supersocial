package me.steffenjacobs.supersocial.persistence.exception;

import java.util.UUID;

/**
 * Should be fired if a
 * {@link me.steffenjacobs.supersocial.domain.entity.UserGroup}is not on an
 * {@link me.steffenjacobs.supersocial.domain.entity.AccessControlList} but
 * should be updated or removed.S
 * 
 * @author Steffen Jacobs
 */
public class UserGroupNotOnAccessControlListExceptiopn extends RuntimeException {
	private static final long serialVersionUID = -2733754274111660301L;

	public UserGroupNotOnAccessControlListExceptiopn(UUID userGroupId, UUID aclId) {
		super(String.format("User '%s' is not on ACL '%s'.", userGroupId, aclId));
	}

}
