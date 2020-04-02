package me.steffenjacobs.supersocial.domain.dto;

import java.util.Set;
import java.util.UUID;

import me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount;

/** @author Steffen Jacobs */
public class SocialMediaAccountDTO implements WithErrorDTO {

	private UUID id;
	private String displayName;
	private int platformId;
	private String error;
	private Set<CredentialDTO> credentials;

	public SocialMediaAccountDTO() {

	}

	public SocialMediaAccountDTO(String error) {
		this.error = error;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	
	public Set<CredentialDTO> getCredentials() {
		return credentials;
	}
	
	public void setCredentials(Set<CredentialDTO> credentials) {
		this.credentials = credentials;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SocialMediaAccountDTO [id=").append(id).append(", displayName=").append(displayName).append(", platformId=").append(platformId).append("]");
		return builder.toString();
	}

	public static SocialMediaAccountDTO fromSocialMediaAccount(SocialMediaAccount account, Set<CredentialDTO> credentials) {
		SocialMediaAccountDTO accountDto = new SocialMediaAccountDTO();
		accountDto.setId(account.getId());
		accountDto.setDisplayName(account.getDisplayName());
		accountDto.setPlatformId(account.getPlatform().getId());
		accountDto.setCredentials(credentials);
		return accountDto;
	}

}
