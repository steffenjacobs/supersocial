package me.steffenjacobs.supersocial.domain.dto;

import java.util.UUID;

/** @author Steffen Jacobs */
public class CredentialDTO {
	private UUID id;
	private String value;
	private String descriptor;
	private boolean omitted;
	private UserGroupDTO userGroup;

	public CredentialDTO() {
		super();
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}

	public boolean isOmitted() {
		return omitted;
	}

	public void setOmitted(boolean omitted) {
		this.omitted = omitted;
	}

	public UserGroupDTO getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(UserGroupDTO userGroup) {
		this.userGroup = userGroup;
	}

}
