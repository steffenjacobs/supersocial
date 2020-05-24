package me.steffenjacobs.supersocial.domain.dto;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import me.steffenjacobs.supersocial.domain.entity.SocialMediaAccount;

/** @author Steffen Jacobs */
public class SocialMediaAccountDTO implements WithErrorDTO, WithAclDTO {

	private UUID id;
	private String displayName;
	private int platformId;
	private String error;
	private Set<CredentialDTO> credentials;
	private List<AclEntryDTO> acl;
	private UUID aclId;

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
		accountDto.setAcl(account.getAccessControlList().getPermittedActions().entrySet().stream().map(e -> new AclEntryDTO(e.getKey().getId(), e.getValue().getMask()))
				.collect(Collectors.toList()));
		accountDto.setAclId(account.getAccessControlList().getId());
		return accountDto;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((credentials == null) ? 0 : credentials.hashCode());
		result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + platformId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SocialMediaAccountDTO other = (SocialMediaAccountDTO) obj;
		if (credentials == null) {
			if (other.credentials != null)
				return false;
		} else if (!credentials.equals(other.credentials))
			return false;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (platformId != other.platformId)
			return false;
		return true;
	}

	public void setAcl(List<AclEntryDTO> acl) {
		this.acl = acl;
	}

	@Override
	public List<AclEntryDTO> getAcl() {
		return this.acl;
	}

	@Override
	public UUID getAclId() {
		return this.aclId;
	}

	public void setAclId(UUID aclId) {
		this.aclId = aclId;
	}

}
