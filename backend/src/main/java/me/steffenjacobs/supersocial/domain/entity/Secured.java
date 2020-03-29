package me.steffenjacobs.supersocial.domain.entity;

/** @author Steffen Jacobs */
public interface Secured {
	SecuredType getSecuredType();

	AccessControlList getAccessControlList();
	
	void setAccessControlList(AccessControlList accessControlList);

}
