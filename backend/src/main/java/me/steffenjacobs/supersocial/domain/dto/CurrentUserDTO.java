package me.steffenjacobs.supersocial.domain.dto;

import java.util.UUID;

import me.steffenjacobs.supersocial.domain.entity.SupersocialUser;

/** @author Steffen Jacobs */
public class CurrentUserDTO {
	private UUID id;
	private String username;
	private int providerId;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setProviderId(int providerId) {
		this.providerId = providerId;
	}

	public int getProviderId() {
		return providerId;
	}

	public static CurrentUserDTO fromUser(SupersocialUser user) {
		CurrentUserDTO dto = new CurrentUserDTO();
		dto.setId(user.getId());
		dto.setUsername(user.getName());
		dto.setProviderId(user.getLoginProvider().getId());
		return dto;
	}
}
