package me.steffenjacobs.supersocial.domain.dto;

import java.util.UUID;

import me.steffenjacobs.supersocial.domain.entity.SupersocialUser;

/** @author Steffen Jacobs */
public class SupersocialUserDTO {
	private UUID uuid;
	private String displayName;

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public static SupersocialUserDTO fromSupersocialUser(SupersocialUser user) {
		SupersocialUserDTO dto = new SupersocialUserDTO();
		dto.setUuid(user.getId());
		dto.setDisplayName(user.getName());
		return dto;
	}

}
