package me.steffenjacobs.supersocial.domain.dto;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import me.steffenjacobs.supersocial.domain.entity.SupersocialUser;

/** @author Steffen Jacobs */
public class CurrentUserDTO implements WithErrorDTO {
	private UUID id;
	private String username;
	private int providerId;
	private Set<UserConfigurationDTO> config;
	private String error;

	public CurrentUserDTO() {
	}

	public CurrentUserDTO(String error) {
		this.error = error;
	}

	public UUID getId() {
		return id;
	}

	@Override
	public String getError() {
		return error;
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

	public Set<UserConfigurationDTO> getConfig() {
		return config;
	}

	public void setConfig(Set<UserConfigurationDTO> config) {
		this.config = config;
	}

	public static CurrentUserDTO fromUser(SupersocialUser user) {
		CurrentUserDTO dto = new CurrentUserDTO();
		dto.setId(user.getId());
		dto.setUsername(user.getName());
		dto.setProviderId(user.getLoginProvider().getId());
		dto.setConfig(user.getUserConfigurations().stream().map(UserConfigurationDTO::fromConfiguration).collect(Collectors.toSet()));
		return dto;
	}
}
