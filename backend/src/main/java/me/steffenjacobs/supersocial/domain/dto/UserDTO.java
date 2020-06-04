package me.steffenjacobs.supersocial.domain.dto;

import java.util.UUID;

import me.steffenjacobs.supersocial.domain.entity.SupersocialUser;

/** @author Steffen Jacobs */
public class UserDTO {
	private UUID id;
	private String name;

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static UserDTO fromUser(SupersocialUser user) {
		UserDTO res = new UserDTO();
		res.setId(user.getId());
		res.setName(user.getName());
		return res;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		UserDTO other = (UserDTO) obj;
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
		return true;
	}
}
