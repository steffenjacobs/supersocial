package me.steffenjacobs.supersocial.domain.dto;

import java.util.UUID;

import me.steffenjacobs.supersocial.domain.entity.UserGroup;

/** @author Steffen Jacobs */
public class UserGroupDTO {
	private UUID id;
	private String name;

	private UserGroupDTO() {

	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static UserGroupDTO fromUserGroup(UserGroup userGroup) {
		UserGroupDTO dto = new UserGroupDTO();
		dto.setId(userGroup.getId());
		dto.setName(userGroup.getName());
		return dto;
	}
}
