package me.steffenjacobs.supersocial.domain.dto;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import me.steffenjacobs.supersocial.domain.entity.SupersocialUser;
import me.steffenjacobs.supersocial.domain.entity.UserGroup;

/** @author Steffen Jacobs */
public class UserGroupDTO implements WithErrorDTO {
	private UUID id;
	private String name;
	private String error;
	private Set<UUID> users;

	public UserGroupDTO(String error) {
		this.error = error;
	}

	public UserGroupDTO() {

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

	public Set<UUID> getUsers() {
		return users;
	}

	public void setUsers(Set<UUID> users) {
		this.users = users;
	}

	public static UserGroupDTO fromUserGroup(UserGroup userGroup) {
		UserGroupDTO dto = new UserGroupDTO();
		dto.setId(userGroup.getId());
		dto.setName(userGroup.getName());
		// TODO: decide if this should be pruned
		dto.setUsers(userGroup.getUsers().stream().map(SupersocialUser::getId).collect(Collectors.toSet()));
		return dto;
	}

	@Override
	public String getError() {
		return error;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((error == null) ? 0 : error.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((users == null) ? 0 : users.hashCode());
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
		UserGroupDTO other = (UserGroupDTO) obj;
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (users == null) {
			if (other.users != null)
				return false;
		} else if (!users.equals(other.users))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UserGroupDTO [id=").append(id).append(", name=").append(name).append(", error=").append(error).append(", users=").append(users).append("]");
		return builder.toString();
	}

}
