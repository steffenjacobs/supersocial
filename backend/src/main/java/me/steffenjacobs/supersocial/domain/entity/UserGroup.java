package me.steffenjacobs.supersocial.domain.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;

/** @author Steffen Jacobs */
@Entity
public class UserGroup implements Secured {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id", updatable = false, nullable = false)
	private UUID id;

	@ManyToMany
	private Set<SupersocialUser> users = new HashSet<>();

	@OneToOne
	private AccessControlList accessControlList;

	public UUID getId() {
		return id;
	}

	public Set<SupersocialUser> getUsers() {
		return users;
	}

	public void setUsers(Set<SupersocialUser> users) {
		this.users = users;
	}

	@Override
	public SecuredType getSecuredType() {
		return SecuredType.UserGroup;
	}

	@Override
	public AccessControlList getAccessControlList() {
		return accessControlList;
	}

	@Override
	public void setAccessControlList(AccessControlList accessControlList) {
		this.accessControlList = accessControlList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		UserGroup other = (UserGroup) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
