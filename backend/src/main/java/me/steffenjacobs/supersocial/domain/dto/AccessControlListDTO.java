package me.steffenjacobs.supersocial.domain.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.steffenjacobs.supersocial.domain.entity.AccessControlList;

/** @author Steffen Jacobs */
public class AccessControlListDTO implements WithErrorDTO {

	private String error;
	private UUID id;
	private Map<UUID, Integer> permittedActions = new HashMap<>();

	public AccessControlListDTO(String error) {
		this.error = error;
	}

	public AccessControlListDTO() {
	}

	public String getError() {
		return error;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Map<UUID, Integer> getPermittedActions() {
		return permittedActions;
	}

	public void setPermittedActions(Map<UUID, Integer> permittedActions) {
		this.permittedActions = permittedActions;
	}

	public static AccessControlListDTO fromAccessControlList(AccessControlList acl) {
		AccessControlListDTO dto = new AccessControlListDTO();
		dto.setId(acl.getId());
		acl.getPermittedActions().forEach((g, a) -> dto.getPermittedActions().put(g.getId(), a.getMask()));
		return dto;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((error == null) ? 0 : error.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((permittedActions == null) ? 0 : permittedActions.hashCode());
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
		AccessControlListDTO other = (AccessControlListDTO) obj;
		if (error == null) {
			if (other.error != null)
				return false;
		} else if (!error.equals(other.error))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (permittedActions == null) {
			if (other.permittedActions != null)
				return false;
		} else if (!permittedActions.equals(other.permittedActions))
			return false;
		return true;
	}

}
